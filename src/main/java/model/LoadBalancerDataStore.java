package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Constants;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that holds all the information at loadBalancer.
 * @author nilimajha
 */
public class LoadBalancerDataStore {
    private static final Logger logger = LogManager.getLogger(LoadBalancerDataStore.class);
    private volatile int id = 0;
    private MembershipTable membershipTable;
    private static LoadBalancerDataStore loadBalancerDataStore = null;
    private final ReentrantReadWriteLock leaderInfoLock = new ReentrantReadWriteLock();

    /**
     * private constructor to make this class Singleton.
     */
    private LoadBalancerDataStore() {
        membershipTable = MembershipTable.getMembershipTable(Constants.LOAD_BALANCER);
    }

    /**
     * make sure that onl one instance on this class is created.
     * @return lodBalancerStore
     */
    public static LoadBalancerDataStore getLoadBalancerDataStore() {
        if (loadBalancerDataStore == null) {
            loadBalancerDataStore = new LoadBalancerDataStore();
        }
        return loadBalancerDataStore;
    }
    /**
     * increment id and return incremented value.
     * @return id
     */
    public synchronized int getId() {
        id += 1;
        return id;
    }

    /**
     * add new member into the membership table.
     * @param memberId
     * @param memberName
     * @param memberIP
     * @param memberPort
     */
    public void addNewMemberIntoMembershipTable(int memberId, String memberName, String memberIP, int memberPort) {
        BrokerInfo brokerInfo = new BrokerInfo(memberName, memberId, memberIP, memberPort);
//        logger.info("\nAdding new broker into the memberShipList. model.LoadBalancerDataStore.");
//        logger.info("\nAdded Id : " + brokerInfo.getBrokerId() + " name : " + brokerInfo.getBrokerName() +
//                " IP : " + brokerInfo.getBrokerIP() + " port : " + brokerInfo.getBrokerPort());
        membershipTable.addMember(memberId, brokerInfo);
    }

    /**
     * method updates the leader Info.
     * @param leaderId
     * @return true
     */
    public boolean updateLeaderInfo(int leaderId) {
        leaderInfoLock.writeLock().lock();
        membershipTable.updateLeader(leaderId);
        leaderInfoLock.writeLock().unlock();
        return true;
    }

    /**
     * returns the leaderInfo
     * @return currentLeaderInfo
     */
    public BrokerInfo getLeaderInfo() {
        leaderInfoLock.readLock().lock();
        BrokerInfo currentLeaderBrokerInfo = membershipTable.getLeaderInfo();
        leaderInfoLock.readLock().unlock();
        return currentLeaderBrokerInfo;
    }

    /**
     * returns all the active broker information in a list.
     * @return activeBrokersInfo
     */
    public ArrayList<BrokerInfo> getMembershipInfo() {
        leaderInfoLock.readLock().lock();
        ArrayList<BrokerInfo> activeBrokersInfo= new ArrayList<>();
        ConcurrentHashMap<Integer, BrokerInfo> allMembersInfo = membershipTable.getMembershipInfo();
        for (Map.Entry<Integer, BrokerInfo> eachEntry : allMembersInfo.entrySet()) {
                activeBrokersInfo.add(eachEntry.getValue());
//            logger.info("\nGetting Id : " + eachEntry.getValue().getBrokerId() + " name : " + eachEntry.getValue().getBrokerName() +
//                    " IP : " + eachEntry.getValue().getBrokerIP() + " port : " + eachEntry.getValue().getBrokerPort());
        }
        leaderInfoLock.readLock().unlock();
        return activeBrokersInfo;
    }

    /**
     * returns the random broker info from the memberShipList.
     * @return brokerInfo
     */
    public BrokerInfo getRandomFollowerBrokerInfo() {
        return membershipTable.getRandomFollowerBrokerInfo();
    }

    /**
     * remove the member broker from the membership list.
     * @return true
     */
    public boolean markMemberDown(int failedMemberId) {
        leaderInfoLock.writeLock().lock();
        membershipTable.markMemberFailed(failedMemberId);
        leaderInfoLock.writeLock().unlock();
        return true;
    }

}
