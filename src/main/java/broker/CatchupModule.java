package broker;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import model.Data;
import model.MembershipTable;
import model.MessageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.CatchupPullRequest;
import proto.ReplicateMessage;
import util.Constants;

import java.util.List;
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
    private AtomicLong offset = new AtomicLong(-1);
    private String pushBasedConsumerTopic = null;
    private BrokerInfo connectionBrokerInfo;
    private String brokerConnectionType;
    private MembershipTable membershipTable;
    private HeartBeatModule heartBeatModule;

    /**
     * Constructor that initialises connection.Connection class object and also model.Data
     * @param connection
     */
    public CatchupModule(String brokerName, Connection connection, BrokerInfo thisBrokerInfo, String connectionWith,
                            BrokerInfo connectionBrokerInfo, String brokerConnectionType, List<String> catchupTopics) {
        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] broker.RequestProcessor for connection of type : " + brokerConnectionType);
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.connection = connection;
        this.connectionWith = connectionWith;
        this.connectionBrokerInfo = connectionBrokerInfo;
        this.brokerConnectionType = brokerConnectionType;
        this.catchupTopics = catchupTopics;
        this.data = Data.getData(thisBrokerInfo);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule();
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
        connection.closeConnection();
    }
}
