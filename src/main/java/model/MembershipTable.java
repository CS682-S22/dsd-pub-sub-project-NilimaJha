package model;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.ReplicateMessage;
import util.Constants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Membership table keeps the information of all the broker members and also the leader.
 * It is a singleton class.
 * @author nilimajha
 */
public class MembershipTable {
    private static final Logger logger = LogManager.getLogger(MembershipTable.class);
    private String storedAt;
    private ConcurrentHashMap<Integer, BrokerInfo> membershipInfo;
    private volatile int leaderId;
    private static MembershipTable membershipTable = null;
    private List<Integer> failedMembersIdList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor
     */
    private MembershipTable(String storedAt) {
        this.storedAt = storedAt;
        this.membershipInfo = new ConcurrentHashMap<>();
        this.leaderId = 0;
    }

    /**
     * if the Data object is already initialised the reference of that object is passed.
     * if the object is not yet created then it will create an instance of it are return.
     * @return Data
     */
    public synchronized static MembershipTable getMembershipTable(String storedAt) {
        if (membershipTable == null) {
            membershipTable = new MembershipTable(storedAt);
        }
        return membershipTable;
    }

    /**
     * add new member in membershipTable.
     * @param id
     * @param brokerInfo
     * @return
     */
    public boolean addMember(int id, BrokerInfo brokerInfo) {
        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Adding new broker into the memberShipList. MemberShipTable.");
        if (!membershipInfo.containsKey(id)) {
            BrokerInfo previousBrokerInfo = membershipInfo.putIfAbsent(id, brokerInfo);
            if (previousBrokerInfo == null) {
                logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] New Member is added in the model.MembershipTable.size : " + membershipInfo.size());
            }
        } else {
            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] broker.Broker already exist in the list. MemberShipTable. size : " + membershipInfo.size());
        }
        return true;
    }

    /**
     * add the dataConnection connection fot the member with given memberId.
     * @param memberId
     * @param dataConnection
     */
    public void addDataConnectionToMember(int memberId, Connection dataConnection) {
        membershipInfo.get(memberId).setDataConnection(dataConnection);
    }

    /**
     * remove the member from the membershipList.
     * @param id ID of broker.Broker
     */
    public void markMemberFailed(int id) {
        logger.info("\nMember failed :" + id);
        if (storedAt.equals(Constants.BROKER) && leaderId == id) {
            //election will happen
            //remove current leader info
            leaderId = -1;
            membershipInfo.remove(id);
        } else if (storedAt.equals(Constants.LOAD_BALANCER) && leaderId == id) {
            leaderId = -1;
            membershipInfo.remove(id);
        } else {
            membershipInfo.remove(id);
            if (storedAt.equals(Constants.BROKER)) {
                logger.info("\nAdding failed member into failedMemberIdList.");
                failedMembersIdList.add(id);
            }
        }
    }

    /**
     * removes all the entries from the failedMemberList.
     */
    public void resetFailedMembersList() {
        logger.info("\nReSetting failedMemberList.");
        failedMembersIdList.clear();
    }

    /**
     * updates variable leaderId to store the new leader's id.
     * @param leaderId new leader ID
     */
    public void updateLeader(int leaderId) {
        logger.info("\nLeader Before Update : " + this.leaderId);
        this.leaderId = leaderId;
        logger.info("\nLeader After Update : " + this.leaderId);
    }

    /**
     * returns the current leader information from the membershipTable.
     * @return BrokerInfo leader broker info.
     */
    public BrokerInfo getLeaderInfo() {
        logger.info("\nSize : " + membershipInfo.size() + " LeaderId : " + leaderId);
        return membershipInfo.get(leaderId);
    }

    /**
     * getter for the attribute leaderId
     * @return leaderId
     */
    public int getLeaderId() {
        return leaderId;
    }

    /**
     * getter for attribute membershipInfo hashmap.
     * @return membershipInfo
     */
    public ConcurrentHashMap<Integer, BrokerInfo> getMembershipInfo() {
        return membershipInfo;
    }

    /**
     * checks if the table contains member with given memberID.
     * @return true/false
     */
    public boolean isMember(int memberID) {
        return membershipInfo.containsKey(memberID);
    }

    /**
     * methods sends the message/actual_data over the DataConnection established between two broker.
     * @param message message published
     */
    public void sendSynchronousData(long thisMessageOffset, String topic, byte[] message, long expectedAckNumber) {
        Any any = Any.pack(ReplicateMessage.ReplicateMessageDetails.newBuilder()
                .setSynchronous(true)
                .setMessageId(thisMessageOffset)
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(message))
                .build());
        for (Map.Entry<Integer, BrokerInfo> eachMember : membershipInfo.entrySet()) {
            logger.info("[ThreadId : " + Thread.currentThread().getId() + " Calling member.sendData.");
            eachMember.getValue().sendOverDataConnection(any.toByteArray(), expectedAckNumber);
        }
    }

    /**
     * returns list of all the failed member in this round.
     * @return failedMembersIdList
     */
    public List<Integer> getFailedMembersIdList() {
        return failedMembersIdList;
    }

    /**
     * used at loadBalancer to return one of the follower broker information
     * to the consumer, To facilitate read from follower.
     * @return BrokerInfo
     */
    public BrokerInfo getRandomFollowerBrokerInfo() {
        Random rand = new Random();
        BrokerInfo brokerInfo = null;
        while (brokerInfo == null && membershipInfo.size() > 0) {
            logger.info("\n Inside the loop.");
            int bound = membershipInfo.keySet().size() + 1;
            int val = rand.nextInt(bound);
            logger.info("\n bound is : " + bound + " val : " + val);
            brokerInfo = membershipInfo.get(val);
            if (brokerInfo != null && membershipInfo.size() > 1 && leaderId == brokerInfo.getBrokerId()) {

                logger.info("\n brokerInfo : " + brokerInfo + " membershipInfoSize : " + membershipInfo.size() + " leaderId =" + leaderId + " brokerSelected brokerId : " + brokerInfo.getBrokerId());

                brokerInfo = null;
                logger.info("\nSetting broker null as leader was selected. ");
            } else if (brokerInfo != null){
                break;
            }
        }
        if (brokerInfo != null) {
            logger.info("\nBroker selected for read is " + brokerInfo.getBrokerId() + " Total member : " + membershipInfo.size());
        } else {
            logger.info("\nBroker selected is null. Total member : " + membershipInfo.size());
        }
        return brokerInfo;
    }
}
