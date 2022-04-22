package broker;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import model.DBSnapshot;
import model.Data;
import model.MembershipTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;
import util.Constants;
import util.Utility;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class which will handle election related process and information at the broker.
 * @author nilimajha 
 */
public class ElectionModule {
    private static final Logger logger = LogManager.getLogger(ElectionModule.class);
    private MembershipTable membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
    private BrokerInfo thisBrokerInfo;
    private String loadBalancerIp;
    private int loadBalancerPort;
    private ConcurrentHashMap<Integer, DBSnapshot> membersSnapshotMap = new ConcurrentHashMap<>(); //after election
    private volatile boolean electionStatus = false;
    private volatile boolean electionResponseReceived = false;
    private volatile boolean victoryMessageReceived = false;
    private static ElectionModule electionModule = null;
    private final Object electionResponseWaitObj = new Object();
    private final Object victoryMessageWaitObj = new Object();

    /**
     * private Constructor as this class is singleton.
     */
    private ElectionModule(BrokerInfo thisBrokerInfo, String loadBalancerIp, int loadBalancerPort) {
        this.thisBrokerInfo = thisBrokerInfo;
        this.loadBalancerIp = loadBalancerIp;
        this.loadBalancerPort = loadBalancerPort;
    }

    /**
     * method that make sure that during entire run only one instance of this class is created.
     * @return electionModule
     */
    synchronized static ElectionModule getElectionModule(BrokerInfo thisBrokerInfo, String loadBalancerIp, int loadBalancerPort) {
        if (electionModule == null) {
            electionModule = new ElectionModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        }
        return electionModule;
    }

    /**
     * method sends request for the current Snapshot from all the member broker from the
     * MembershipList over the DataConnection with that member..
     * @return true;
     */
    public boolean getEachMemberSnapshot() {
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Inside getEachMemberSnapshot method.");
        Any snapshotRequest = Any.pack(SnapshotRequest.SnapshotRequestDetails.newBuilder().setMemberId(thisBrokerInfo.getBrokerId()).build());

        for (Map.Entry<Integer, BrokerInfo> eachMember : membershipTable.getMembershipInfo().entrySet()) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending Snapshot request over Data-Connection to member with Id :" + eachMember.getKey());
            eachMember.getValue().sendOverDataConnection(snapshotRequest.toByteArray());
            byte[] receivedMessage = eachMember.getValue().receiveOverDataConnection();
            if (receivedMessage != null) {
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received DBSnapshot from member with Id : " + eachMember.getKey());
                Any any = null;
                try {
                    any = Any.parseFrom(receivedMessage);
                    if (any.is(proto.DBSnapshot.DBSnapshotDetails.class)) {
                        proto.DBSnapshot.DBSnapshotDetails dbSnapshotDetails =
                                any.unpack(proto.DBSnapshot.DBSnapshotDetails.class);
                        List<ByteString> topicSnapshotList = dbSnapshotDetails.getTopicSnapshotList();
                        DBSnapshot dbSnapshot = new DBSnapshot(eachMember.getKey());
                        for (ByteString eachTopicSnapshot : topicSnapshotList) {
                            Any any1 = Any.parseFrom(eachTopicSnapshot);
                            if (any1.is(TopicSnapshot.TopicSnapshotDetails.class)) {
                                TopicSnapshot.TopicSnapshotDetails topicSnapshotDetails = any1.unpack(TopicSnapshot.TopicSnapshotDetails.class);
                                model.TopicSnapshot topicSnapshot = new model.TopicSnapshot(topicSnapshotDetails.getTopic(), topicSnapshotDetails.getOffset());
                                dbSnapshot.addTopicSnapshot(topicSnapshotDetails.getTopic(), topicSnapshot);
                            }
                        }
                        membersSnapshotMap.put(eachMember.getKey(), dbSnapshot);
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred. Error Message : " + e.getMessage());
                }

            }
        }
        return true;
    }

    /**
     * using this method leader broker sends StartSyncUpMessage to all the follower over the DataConnection.
     */
    public boolean sendStartSyncUpMessage() {
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Inside sendStartSyncMessage.");
        Data data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        DBSnapshot myDBSnapshot = data.getSnapshot(); // thisBrokerDataSnapshot.
        byte[] startSyncUpMessage = Utility.getDBSnapshotMessageBytes(myDBSnapshot, thisBrokerInfo.getBrokerId(), Constants.START_SYNC);
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] MemberList : " + membersSnapshotMap.entrySet().size());
        for (Map.Entry<Integer, DBSnapshot> eachMemberSnapshot : membersSnapshotMap.entrySet()) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending Start Sync-Up Message to Member with Id : " + eachMemberSnapshot.getKey());
            membershipTable.getMembershipInfo().get(eachMemberSnapshot.getKey()).sendOverDataConnection(startSyncUpMessage);
        }
        return true;
    }

    /**
     * method starts election process my sending Election message.
     * Election algorithm implemented here is bully algorithm.
     */
    public synchronized void startElection() {
        if (electionStatus) {
            // sending election message to all the member in the memberTable with lower memberId (lowerMemberId member will be leader).
            Any any = Any.pack(ElectionMessage.ElectionMessageDetails.newBuilder().setMessageSenderId(thisBrokerInfo.getBrokerId()).build());
            electionResponseReceived = false;
            victoryMessageReceived = false;
            for (Map.Entry<Integer, BrokerInfo> eachMember : membershipTable.getMembershipInfo().entrySet()) {
                if (eachMember.getKey() < thisBrokerInfo.getBrokerId()) {
                    // send the election message
                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending ElectionMessage to Member with Id " + eachMember.getKey());
                   eachMember.getValue().sendOverHeartbeatConnection(any.toByteArray());
                }
            }

            //wait to receive election response message from the members to whom election message was sent.
            synchronized (electionResponseWaitObj) {
                try {
                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Waiting for Election Response Message from Member.");
                    electionResponseWaitObj.wait(60000);
                } catch (InterruptedException e) {
                    logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                }
            }
            if (!electionResponseReceived && !victoryMessageReceived) {
//                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] No Victory Message or Response Message for the Election message is received. Declaring itself as leader.");
                // no response received. send victory message
                Any victoryMessage = Any.pack(VictoryMessage.VictoryMessageDetails.newBuilder()
                        .setNewLeaderId(thisBrokerInfo.getBrokerId())
                        .setNewLeaderName(thisBrokerInfo.getBrokerName())
                        .setNewLeaderIp(thisBrokerInfo.getBrokerIP())
                        .setNewLeaderPort(thisBrokerInfo.getBrokerPort())
                        .build());

                membershipTable.updateLeader(thisBrokerInfo.getBrokerId());
                thisBrokerInfo.setLeader(true);
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] No Victory Message or Response Message for the Election message is received. Sending Victory message to all the members.");
                for (Map.Entry<Integer, BrokerInfo> eachMember : membershipTable.getMembershipInfo().entrySet()) {
                    eachMember.getValue().sendOverHeartbeatConnection(victoryMessage.toByteArray());
                }

                // starting syncUpProcess.
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Starting Catchup Process. Setting Catchup status true.");
                thisBrokerInfo.setCatchupMode(true);
//                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Calling getEachMemberSnapshot method.");
                getEachMemberSnapshot(); // getting eachMember's current snapshot.
                CatchupModule catchupModule = new CatchupModule(thisBrokerInfo.getBrokerName(), thisBrokerInfo, loadBalancerIp,
                loadBalancerPort, membersSnapshotMap);
                catchupModule.doSyncUpNewLeader();
                thisBrokerInfo.setCatchupMode(false);
                sendStartSyncUpMessage(); // sending each member message saying sync done.

//                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Connecting to Load Balancer.");
                Connection loadBalancerConnection = null;
                try {
                    loadBalancerConnection = Utility.establishConnection(loadBalancerIp, loadBalancerPort);
                } catch (ConnectionClosedException e) {
                    logger.info(e.getMessage());
                }
                if (loadBalancerConnection != null) {
//                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + " Connected to loadBalancer.");
                    //sending victory message to the loadBalancer.
                    boolean leaderUpdated = Utility.sendUpdateLeaderMessageToLB(loadBalancerConnection, thisBrokerInfo.getBrokerName(),
                            thisBrokerInfo.getBrokerId());
                    loadBalancerConnection.closeConnection();
                }
            } else {
                //wait to receive election victory  message from the members to whom election message was sent.
                if (!victoryMessageReceived) {
                    synchronized (victoryMessageWaitObj) {
                        try {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Waiting for VictoryMessage from New Leader.");
                            victoryMessageWaitObj.wait(60000);
                        } catch (InterruptedException e) {
                            logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                        }
                    }
                }
                if (!victoryMessageReceived) {
                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] No Victory message received. Restarting the election.");
                    startElection();
                }
            }
            electionStatus = false;
        }
    }

    /**
     * this method is used to notify the thread waiting for the receiving
     * ElectionResponse message from the members with higher MemberId.
     */
    public void notifyElectionResponseReceived() {
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + " Notifying ElectionMessageResponse Received.");
        synchronized (electionResponseWaitObj) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Notifying thread waiting for ElectionMessageResponse.");
            //logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Notifying the thread about timeout.");
            electionResponseWaitObj.notify();
        }
    }

    /**
     * this method is used to notify the thread waiting for the
     * Victory Message from the members with higher MemberId.
     */
    public void notifyVictoryMessageReceived() {
        synchronized (victoryMessageWaitObj) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Notifying thread waiting for VictoryMessage.");
            victoryMessageWaitObj.notify();
        }
        synchronized (electionResponseWaitObj) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Notifying thread waiting for ElectionMessageResponse as Victory message received.");
            electionResponseWaitObj.notify();
        }
    }

    /**
     * setter for the attribute electionStatus.
     * @param electionStatus true/false
     */
    public void setElectionStatus(boolean electionStatus) {
        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Setting election status to be " + electionStatus);
        this.electionStatus = electionStatus;
    }

    /**
     * setter for the attribute electionResponseReceived.
     * @param electionResponseReceived true/false
     */
    public void setElectionResponseReceived(boolean electionResponseReceived) {
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Setting election status to be " + electionStatus);
        this.electionResponseReceived = electionResponseReceived;
    }

    /**
     * setter for the attribute victoryMessageReceived.
     * @param victoryMessageReceived true/false
     */
    public void setVictoryMessageReceived(boolean victoryMessageReceived) {
//        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + " Setting Victory Message Received status to be : " + victoryMessageReceived);
        this.victoryMessageReceived = victoryMessageReceived;
    }

    /**
     * getter for the attribute victoryMessageReceived
     * @return victoryMessageReceived true/false
     */
    public boolean getVictoryMessageReceived() {
        return victoryMessageReceived;
    }


    /**
     * getter for the attribute electionResponseReceived
     * @return electionResponseReceived true/false
     */
    public boolean getElectionResponseReceived() {
        return electionResponseReceived;
    }

    /**
     * getter for the attribute electionStatus
     * @return electionStatus true/false
     */
    public boolean getElectionStatus() {
        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Returning election status which is " + electionStatus);
        return electionStatus;
    }
}
