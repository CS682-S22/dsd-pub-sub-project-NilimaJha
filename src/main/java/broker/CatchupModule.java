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
import util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] broker.RequestProcessor for connection of type : " + brokerConnectionType);
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
     *
     * @param brokerName
     * @param thisBrokerInfo
     * @param loadBalancerIp
     * @param loadBalancerPort
     * @param leaderDbSnapshot
     */
    public CatchupModule(String brokerName, BrokerInfo thisBrokerInfo, String loadBalancerIp,
                         int loadBalancerPort, DBSnapshot leaderDbSnapshot) {
        logger.info("\nInside Catchup module.");
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.dbSnapshot = leaderDbSnapshot;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     *
     * @param brokerName
     * @param thisBrokerInfo
     * @param loadBalancerIp
     * @param loadBalancerPort
     * @param membersSnapshotMap
     */
    public CatchupModule(String brokerName, BrokerInfo thisBrokerInfo, String loadBalancerIp,
                         int loadBalancerPort, ConcurrentHashMap<Integer, DBSnapshot> membersSnapshotMap) {
        logger.info("\nInside Catchup module.");
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.membersSnapshotMap = membersSnapshotMap;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     * 
     */
    public void doSyncUpNewLeader() {
        for (Map.Entry<Integer, DBSnapshot> eachMemberSnapshot : membersSnapshotMap.entrySet()) {
            DBSnapshot myDBSnapshot = data.getSnapshot(); // thisBrokerDataSnapshot.
            logger.info("\nLeader with Id-" + thisBrokerInfo.getBrokerId() + " is Syncing up with the Broker with Id-" + eachMemberSnapshot.getKey());
            for (Map.Entry<String, TopicSnapshot> eachTopicSnapshot : eachMemberSnapshot.getValue().getTopicSnapshotMap().entrySet()) {
                logger.info("\nLeader with Id-" + thisBrokerInfo.getBrokerId() + " is Syncing up with the Broker with Id-" + eachMemberSnapshot.getKey() + " on topic " + eachTopicSnapshot.getKey());
                if (!myDBSnapshot.getTopicSnapshotMap().containsKey(eachTopicSnapshot.getKey()) ||
                        eachTopicSnapshot.getValue().getOffset() > myDBSnapshot.getTopicSnapshotMap()
                                .get(eachTopicSnapshot.getKey()).getOffset()) {
                    logger.info("\n Sync up is needed.");
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
                                        logger.info("\n received message. Total message : " + replicateMessageDetails.getTotalMessage());
                                        if (replicateMessageDetails.getTotalMessage() == 0) {
                                            logger.info("\n Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                            data.getMessageInfoForTheTopic(eachTopicSnapshot.getKey()).setUpToDate(true);
                                            upToDateOnThisTopic = true;
                                            break;
                                        } else {
                                            for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                                byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                                logger.info("\n checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                                if (!data.getMessageInfoForTheTopic(eachTopicSnapshot.getKey()).getIsUpToDate()) {
                                                    logger.info("\n Adding catchup message on topic : " + replicateMessageDetails.getTopic());
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
                                    logger.error("\nInvalidProtocolBufferException occurred while decoding the message. Error Message : " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
        thisBrokerInfo.setCatchupMode(true);
    }

    /**
     *
     */
    public void doSyncUpFollower() {
        catchupTopics = new ArrayList<>(dbSnapshot.getTopicSnapshotMap().keySet());
        for (String catchupTopic : catchupTopics) {
            MessageInfo currentTopicMessageInfo = data.getMessageInfoForTheTopic(catchupTopic);
            AtomicLong offset = new AtomicLong(0);
            if (data.getTopicToMessageMap().containsKey(catchupTopic)) {
                offset = new AtomicLong(data.getTopicToMessageMap().get(catchupTopic).getLastOffSet());
            }
            logger.info("\nPulling data on topic : " + catchupTopic + ". currentTopic.getUptodate : "
                    + currentTopicMessageInfo.getIsUpToDate());
            logger.info("\n LeaderId after election : " + membershipTable.getLeaderId());
            while (!currentTopicMessageInfo.getIsUpToDate() && membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).isDataConnectionConnected()) {
                byte[] pullRequest = createPullRequestForData(catchupTopic, offset.get());
                boolean responseReceived = false;
                while (!responseReceived) {
                    try {
                        logger.info("\n pulling data from leader. about topic : " + catchupTopic + " offset : " + offset.get());
                        membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).sendOverDataConnection(pullRequest);
                        byte[] response = membershipTable.getMembershipInfo().get(membershipTable.getLeaderId()).receiveOverDataConnection();
                        if (response != null) {
                            Any any = Any.parseFrom(response);
                            if (any.is(ReplicateMessage.ReplicateMessageDetails.class)) {
                                ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                        any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                                responseReceived = true;
                                logger.info("\n received message. Total message : " + replicateMessageDetails.getTotalMessage());
                                if (replicateMessageDetails.getTotalMessage() == 0) {
                                    logger.info("\n Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                    currentTopicMessageInfo.setUpToDate(true);
                                    break;
                                } else {
                                    for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                        byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                        logger.info("\n checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                        if (!currentTopicMessageInfo.getIsUpToDate()) {
                                            logger.info("\n Adding catchup message on topic : " + replicateMessageDetails.getTopic());
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

    @Override
    public void run() {
        if (brokerConnectionType.equals(Constants.CATCHUP_CONNECTION)) {
            logger.info("\n pulling data from leader.");
            pullDataFromLeaderToCatchup();
            logger.info("\n pulling data done.");
        }
    }

    /**
     *
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
     *
     *
     */
    public void pullDataFromLeaderToCatchup() {
        for (String catchupTopic : catchupTopics) {
            MessageInfo currentTopicMessageInfo = data.getMessageInfoForTheTopic(catchupTopic);
            AtomicLong offset = new AtomicLong(0);
            logger.info("\nPulling data from topic : " + catchupTopic + ". currentTopic.getUptodate : "
                    + currentTopicMessageInfo.getIsUpToDate());
            while (!currentTopicMessageInfo.getIsUpToDate() && connection.connectionIsOpen()) {
                byte[] pullRequest = createPullRequestForData(catchupTopic, offset.get());
                boolean responseReceived = false;
                while (!responseReceived) {
                    try {
                        logger.info("\n pulling data from leader. about topic : " + catchupTopic + " offset : " + offset.get());
                        connection.send(pullRequest);
                        byte[] response = connection.receive();
                        if (response != null) {
                            Any any = Any.parseFrom(response);
                            if (any.is(ReplicateMessage.ReplicateMessageDetails.class)) {
                                ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                        any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                                responseReceived = true;
                                logger.info("\n received message. Total message : " + replicateMessageDetails.getTotalMessage());
                                if (replicateMessageDetails.getTotalMessage() == 0) {
                                    logger.info("\n Total message : " + replicateMessageDetails.getTotalMessage() + " setting up-to-date true for this topic");
                                    currentTopicMessageInfo.setUpToDate(true);
                                    break;
                                } else {
                                    for (int index = 0; index < replicateMessageDetails.getTotalMessage(); index++) {
                                        byte[] actualMessageBytes = replicateMessageDetails.getMessageBatch(index).toByteArray();
                                        logger.info("\n checking if this message is needed to be added catchup message on topic : " + replicateMessageDetails.getTopic());
                                        if (!currentTopicMessageInfo.getIsUpToDate()) {
                                            logger.info("\n Adding catchup message on topic : " + replicateMessageDetails.getTopic());
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
                        logger.error(e.getMessage());
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
