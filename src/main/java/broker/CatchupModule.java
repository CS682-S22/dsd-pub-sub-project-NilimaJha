package broker;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.CatchupPullRequest;
import proto.ReplicateMessage;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class contains method for Leader Catchup Follower.
 * After each election syncUp process will be done with the help of this class.
 * @author nilimajha
 */
public class CatchupModule implements Runnable {
    private static final Logger logger = LogManager.getLogger(CatchupModule.class);
    private String brokerName;
    private BrokerInfo thisBrokerInfo;
    private Connection connection;
    private String connectionWith;
    private volatile int messageId1 = 0;
    private Data data;
    private List<String> catchupTopics;
    private DBSnapshot dbSnapshot;
    private AtomicLong offset = new AtomicLong(-1);
    private String pushBasedConsumerTopic = null;
    private BrokerInfo connectionBrokerInfo;
    private String brokerConnectionType;
    private MembershipTable membershipTable;
    private HeartBeatModule heartBeatModule;
    private ConcurrentHashMap<Integer, DBSnapshot> membersSnapshotMap = new ConcurrentHashMap<>();
    private List<Integer> upToDateMembers = new ArrayList<>();

    /**
     * Constructor that initialises connection.Connection class object and also model.Data
     * @param connection
     */
    public CatchupModule(String brokerName, Connection connection, BrokerInfo thisBrokerInfo, String loadBalancerIp,
                         int loadBalancerPort, String connectionWith, BrokerInfo connectionBrokerInfo,
                         String brokerConnectionType, List<String> catchupTopics) {
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.connection = connection;
        this.connectionWith = connectionWith;
        this.connectionBrokerInfo = connectionBrokerInfo;
        this.brokerConnectionType = brokerConnectionType;
        this.catchupTopics = catchupTopics;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     * Constructor
     * @param brokerName
     * @param thisBrokerInfo
     * @param loadBalancerIp
     * @param loadBalancerPort
     * @param leaderDbSnapshot
     */
    public CatchupModule(String brokerName, BrokerInfo thisBrokerInfo, String loadBalancerIp,
                         int loadBalancerPort, DBSnapshot leaderDbSnapshot) {
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.dbSnapshot = leaderDbSnapshot;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     * Constructor
     * @param brokerName
     * @param thisBrokerInfo
     * @param loadBalancerIp
     * @param loadBalancerPort
     * @param membersSnapshotMap
     */
    public CatchupModule(String brokerName, BrokerInfo thisBrokerInfo, String loadBalancerIp,
                         int loadBalancerPort, ConcurrentHashMap<Integer, DBSnapshot> membersSnapshotMap) {
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.membersSnapshotMap = membersSnapshotMap;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     * this method is called by leaderBroker after election and
     * at it will bring the new leader upToDate.
     */
    public void doSyncUpNewLeader() {
        for (Map.Entry<Integer, DBSnapshot> eachMemberSnapshot : membersSnapshotMap.entrySet()) {
            DBSnapshot myDBSnapshot = data.getSnapshot(); // thisBrokerDataSnapshot.
            logger.info("\nLeader with Id-" + thisBrokerInfo.getBrokerId() + " is Syncing up with the Broker with Id-" + eachMemberSnapshot.getKey());
            for (Map.Entry<String, TopicSnapshot> eachTopicSnapshot : eachMemberSnapshot.getValue().getTopicSnapshotMap().entrySet()) {
                if (!myDBSnapshot.getTopicSnapshotMap().containsKey(eachTopicSnapshot.getKey()) ||
                        eachTopicSnapshot.getValue().getOffset() > myDBSnapshot.getTopicSnapshotMap()
                                .get(eachTopicSnapshot.getKey()).getOffset()) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Leader Catching up with broker " + eachMemberSnapshot.getKey() + " for the Topic '" + eachTopicSnapshot.getKey() + "'");
                    // pulling data on currentTopic
                    long offset = 0;
                    if (myDBSnapshot.getTopicSnapshotMap().containsKey(eachTopicSnapshot.getKey())) {
                        offset = myDBSnapshot.getTopicSnapshotMap().get(eachTopicSnapshot.getKey()).getOffset();
                    }
                    boolean upToDateOnThisTopic = false;
                    while (!upToDateOnThisTopic) {
                        membershipTable.getMembershipInfo().get(eachMemberSnapshot.getKey())
                                .sendOverDataConnection(createPullRequestForData(eachTopicSnapshot.getKey(), offset));
                        if (membershipTable.getMembershipInfo().get(eachMemberSnapshot.getKey()).isDataConnectionConnected()) {
                            byte[] message = membershipTable.getMembershipInfo().get(eachMemberSnapshot.getKey()).receiveOverDataConnection();
                            if (message == null) {
                                upToDateOnThisTopic = true;
                            } else {
                                try {
                                    Any any = Any.parseFrom(message);
                                    if (any.is(ReplicateMessage.ReplicateMessageDetails.class)) {
                                        ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                                any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received message. Total message Received : " + replicateMessageDetails.getTotalMessage());
                                        if (replicateMessageDetails.getTotalMessage() == 0) {
                                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                            data.getMessageInfoForTheTopic(eachTopicSnapshot.getKey()).setUpToDate(true);
                                            upToDateOnThisTopic = true;
                                            break;
                                        } else {
                                            for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                                byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                                if (!data.getMessageInfoForTheTopic(eachTopicSnapshot.getKey()).getIsUpToDate()) {
                                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Adding catchup message on topic : " + replicateMessageDetails.getTopic());
                                                    data.addMessageToTopic(Constants.CATCHUP, eachTopicSnapshot.getKey(), actualMessageBytes, offset);
                                                    offset += actualMessageBytes.length;
                                                } else {
                                                    upToDateOnThisTopic = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } catch (InvalidProtocolBufferException e) {
                                    logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding the message. Error Message : " + e.getMessage());
                                }
                            }
                        }
                    }
                } else {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Leader is Already UpToDate with broker " + eachMemberSnapshot.getKey() + " for the Topic '" + eachTopicSnapshot.getKey() + "'");
                }
            }
        }
        thisBrokerInfo.setCatchupMode(true);
    }

    /**
     * this method is called by all the follower broker after getting startSyncProcess message by the newLeader.
     */
    public void doSyncUpFollower() {
        catchupTopics = new ArrayList<>(dbSnapshot.getTopicSnapshotMap().keySet());
        for (String catchupTopic : catchupTopics) {
            MessageInfo currentTopicMessageInfo = data.getMessageInfoForTheTopic(catchupTopic);
            AtomicLong offset = new AtomicLong(0);
            if (data.getTopicToMessageMap().containsKey(catchupTopic)) {
                offset = new AtomicLong(data.getTopicToMessageMap().get(catchupTopic).getLastOffSet());
            }
            while (!currentTopicMessageInfo.getIsUpToDate() && membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).isDataConnectionConnected()) {
                byte[] pullRequest = createPullRequestForData(catchupTopic, offset.get());
                boolean responseReceived = false;
                while (!responseReceived) {
                    try {
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Pulling data from leader on topic : " + catchupTopic + " from offset : " + offset.get());
                        membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).sendOverDataConnection(pullRequest);
                        byte[] response = membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).receiveOverDataConnection();
                        if (response != null) {
                            Any any = Any.parseFrom(response);
                            if (any.is(ReplicateMessage.ReplicateMessageDetails.class)) {
                                ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                        any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                                responseReceived = true;
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] received message. Total message : " + replicateMessageDetails.getTotalMessage());
                                if (replicateMessageDetails.getTotalMessage() == 0) {
                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                    currentTopicMessageInfo.setUpToDate(true);
                                    break;
                                } else {
                                    for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                        byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                        if (!currentTopicMessageInfo.getIsUpToDate()) {
                                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Adding catchup message on topic : " + replicateMessageDetails.getTopic());
                                            data.addMessageToTopic(Constants.CATCHUP, catchupTopic, actualMessageBytes, offset.get());
                                            offset.addAndGet(actualMessageBytes.length);
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(e.getMessage());
                    }

                }
            }
        }
    }

    /**
     * run method is used in the beginning when new broker joins to sync up with the leader.
     */
    @Override
    public void run() {
        if (brokerConnectionType.equals(Constants.CATCHUP_CONNECTION)) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Pulling data from leader.");
            pullDataFromLeaderToCatchupAtJoining();
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] New member is Up-To-Date with leader.");
        }
    }

    /**
     * method returns byte array form of proto object which will be sent to pull data from another broker.
     * @param topic
     * @return
     */
    private byte[] createPullRequestForData(String topic, long offset) {
        Any any = Any.pack(CatchupPullRequest.CatchupPullRequestDetails.newBuilder()
                .setTopic(topic)
                .setOffset(offset)
                .build());
        return any.toByteArray();
    }

    /**
     * this method is called when a new broker joins to catchup with the leader by pulling data from offset 0.
     */
    public void pullDataFromLeaderToCatchupAtJoining() {
        for (String catchupTopic : catchupTopics) {
            MessageInfo currentTopicMessageInfo = data.getMessageInfoForTheTopic(catchupTopic);
            AtomicLong offset = new AtomicLong(0);
            while (!currentTopicMessageInfo.getIsUpToDate() && connection.connectionIsOpen()) {
                byte[] pullRequest = createPullRequestForData(catchupTopic, offset.get());
                boolean responseReceived = false;
                while (!responseReceived) {
                    try {
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Pulling data from leader. about topic : " + catchupTopic + " offset : " + offset.get());
                        connection.send(pullRequest);
                        byte[] response = connection.receive();
                        if (response != null) {
                            Any any = Any.parseFrom(response);
                            if (any.is(ReplicateMessage.ReplicateMessageDetails.class)) {
                                ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                        any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                                responseReceived = true;
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received message. Total message : " + replicateMessageDetails.getTotalMessage());
                                if (replicateMessageDetails.getTotalMessage() == 0) {
                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                    currentTopicMessageInfo.setUpToDate(true);
                                    break;
                                } else {
                                    for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                        byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                        if (!currentTopicMessageInfo.getIsUpToDate()) {
                                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Adding catchup message on topic : " + replicateMessageDetails.getTopic());
                                            data.addMessageToTopic(Constants.CATCHUP, catchupTopic, actualMessageBytes, offset.get());
                                            offset.addAndGet(actualMessageBytes.length);
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ConnectionClosedException e) {
                        logger.info(e.getMessage());
                        connection.closeConnection();
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\nInvalidProtocolBufferException occurred. Error Message : " + e.getMessage());
                    }

                }
            }
        }
        thisBrokerInfo.setCatchupMode(false);
        connection.closeConnection();
    }

    /**
     * add new memberSnapshot into membersSnapshotMap.
     * @param memberId
     * @param dbSnapshot
     */
    public void addMemberDBSnapshot(int memberId, DBSnapshot dbSnapshot) {
        membersSnapshotMap.putIfAbsent(memberId, dbSnapshot);
    }

    /**
     * reinitialise the memberSnapshotMap.
     */
    public void resetMembersDBSnapshotMap() {
        membersSnapshotMap = new ConcurrentHashMap<>();
    }

    /**
     *
     * @param memberId
     */
    public void addUpToDateMember(int memberId) {
        upToDateMembers.add(memberId);
    }
}
