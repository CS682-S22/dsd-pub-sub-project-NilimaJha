package model;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.ReplicateMessage;
import util.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Membership table keeps the information of all the broker members.
 * @author nilimajha
 */
public class MembershipTable {
    private static final Logger logger = LogManager.getLogger(MembershipTable.class);
    private String storedAt;
    private ConcurrentHashMap<Integer, BrokerInfo> membershipInfo;
    private volatile int leaderId;
    private static MembershipTable membershipTable = null;

    /**
     * Constructor
     */
    private MembershipTable(String storedAt) {
        this.storedAt = storedAt;
        this.membershipInfo = new ConcurrentHashMap<>();
        this.leaderId = -1;
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
     *
     */
    public void addDataConnectionToMember(int memberId, Connection dataConnection) {
        membershipInfo.get(memberId).setDataConnection(dataConnection);
    }

    /**
     * sets the isActive attribute of the brokerInfo associated with the given id as false.
     * @param id ID of broker.Broker
     */
    public void markMemberFailed(int id) {
        logger.info("\nMember failed :" + id);
        if (storedAt.equals(Constants.BROKER) && leaderId == id) {
            //election will happen
            //remove current leader info
            leaderId = -1;
            membershipInfo.remove(id);
            //election true;
        } else if (storedAt.equals(Constants.LOAD_BALANCER) && leaderId == id) {
            leaderId = -1;
            membershipInfo.remove(id);
        } else {
            membershipInfo.remove(id);
        }

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
     * @return model.BrokerInfo leader broker info.
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
            eachMember.getValue().sendData(any.toByteArray(), expectedAckNumber);
        }
    }
}
