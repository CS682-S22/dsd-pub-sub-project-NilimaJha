package broker;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import model.DBSnapshot;
import model.MembershipTable;
import util.Constants;
import model.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;
import util.Utility;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * class to handle connection between broker and producer or consumer.
 * @author nilimajha
 */
public class RequestProcessor implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    private String brokerName;
    private BrokerInfo thisBrokerInfo;
    private Connection connection;
    private Data data;
    private String connectionWith;
    private volatile int messageId = 0;
    private String consumerType;
    private String name;
    private AtomicLong offset = new AtomicLong(-1);
    private String pushBasedConsumerTopic = null;
    private BrokerInfo connectionBrokerInfo;
    private String brokerConnectionType;
    private Timer timer;
    private final Object waitObj = new Object();
    private MembershipTable membershipTable;
    private HeartBeatModule heartBeatModule;
    private ExecutorService electionConductorThread = Executors.newFixedThreadPool(1);

    /**
     * Constructor that initialises connection.Connection class object and also model.Data
     * @param connection
     */
    public RequestProcessor(String brokerName, Connection connection, BrokerInfo thisBrokerInfo, String loadBalancerIp, int loadBalancerPort) {
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.connection = connection;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     * Constructor that initialises connection.Connection class object and also model.Data
     * @param connection
     */
    public RequestProcessor(String brokerName, Connection connection, BrokerInfo thisBrokerInfo,
                            String connectionWith, BrokerInfo connectionBrokerInfo, String brokerConnectionType,
                            String loadBalancerIp, int loadBalancerPort) {
        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] broker.RequestProcessor for connection of type : " + brokerConnectionType);
        this.brokerName = brokerName;
        this.thisBrokerInfo = thisBrokerInfo;
        this.connection = connection;
        this.connectionWith = connectionWith;
        this.connectionBrokerInfo = connectionBrokerInfo;
        this.brokerConnectionType = brokerConnectionType;
        this.data = Data.getData(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
    }

    /**
     *
     */
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                notifyThread();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, Constants.TIMEOUT_IF_DATA_NOT_YET_AVAILABLE);
    }

    /**
     *
     */
    public void notifyThread() {
        timer.cancel();
        synchronized (waitObj) {
            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Notifying the thread about timeout.");
            waitObj.notify();
        }
    }

    /**
     * overriding the run method of the runnable Interface
     */
    @Override
    public void run() {
        start();
    }

    /**
     * start receiving message from the connection and send response accordingly
     * if the connection is from producer then get publish message and add it to the topic
     * if the connection is from pull based consumer then receive pull request and then send message if available
     */
    public void start() {
        // start receiving message
        System.out.println("[Thread Id : " + Thread.currentThread().getId() + "] inside broker.RequestProcessor. connection.Connection With : " + connectionWith);
        while (connectionWith == null) {
            try {
                byte[] receivedMessage = connection.receive();
                if (receivedMessage != null) { // received initial message
                    // call decode packet and then call decode message inside
                    try {
                        Any any = Any.parseFrom(receivedMessage);
                        if (any.is(InitialMessage.InitialMessageDetails.class)) {
                            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] waiting to receive initial setup message. Received something.");
                            parseInitialMessage(any);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred decoding Initial Packet. Error Message : " + e.getMessage());
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                //close the connection.
                connection.closeConnection(); //if connection is closed by other end before sending initial message then close connection.
            }
        }

        if (connectionWith.equals(Constants.PRODUCER)) {
            handlePublisher();
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PULL)) {
            handlePullConsumer();
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PUSH)) {
            handlePushConsumer();
        } else if (connectionWith.equals(Constants.BROKER)) {
            logger.info("[Thread Id : " + Thread.currentThread().getId() + "] connection.Connection With : " + connectionWith + " ConnectionType : " + brokerConnectionType);
            handleBroker();
        }
    }

    /**
     * decode message field of the PacketDetails object as per the type.
     * @param any
     */
    public boolean parseInitialMessage(Any any) {
        logger.info("\n [Thread Id : " + Thread.currentThread().getId() + "] Any of type InitialMessage: "
                + any.is(InitialMessage.InitialMessageDetails.class));
        if (any.is(InitialMessage.InitialMessageDetails.class)) {
            // decode received message
            try {
                InitialMessage.InitialMessageDetails initialMessageDetails =
                        any.unpack(InitialMessage.InitialMessageDetails.class);
                if (connectionWith == null) {
                    if (initialMessageDetails.getConnectionSender().equals(Constants.PRODUCER)) {
                        connectionWith = Constants.PRODUCER;
                        messageId = initialMessageDetails.getMessageId();
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Next message Id : " + messageId);
                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Received InitialPacket from "
                                + initialMessageDetails.getName());
                        // send initial setup ack
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending Initial Setup ACK 1st.");
                        try {
                            connection.send(getInitialSetupACK(Constants.PRODUCER));
                        } catch (ConnectionClosedException e) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] " + e.getMessage());
                            connection.closeConnection();
                        }
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) &&
                            initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PULL)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PULL;    // PULL consumer
                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Received InitialPacket from "
                                + initialMessageDetails.getName() +
                                " consumer.Consumer Type : " + consumerType);
                        // send initial setup ack
                        try {
                            connection.send(getInitialSetupACK(Constants.CONSUMER));
                        } catch (ConnectionClosedException e) {
                            logger.info(e.getMessage());
                            connection.closeConnection();
                        }
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) &&
                            initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PUSH)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PUSH;    // PUSH consumer
                        offset.set(initialMessageDetails.getInitialOffset());
                        pushBasedConsumerTopic = initialMessageDetails.getTopic();
                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Received InitialPacket from "
                                + initialMessageDetails.getName() +
                                " consumer.Consumer Type : " + consumerType +
                                ", InitialOffset : " + offset +
                                ", Topic : " + pushBasedConsumerTopic);
                        // send initial setup ack
                        try {
                            connection.send(getInitialSetupACK(Constants.CONSUMER));
                        } catch (ConnectionClosedException e) {
                            logger.info(e.getMessage());
                            connection.closeConnection();
                        }
                    } else {
                        //initial message is from broker.
                        //if broker is added in membership table then do nothing
                        //else send a connection request and add it to membership table.

                        connectionWith = Constants.BROKER;
                        connectionBrokerInfo = new BrokerInfo(initialMessageDetails.getName(),
                                initialMessageDetails.getBrokerId(),
                                initialMessageDetails.getBrokerIP(),
                                initialMessageDetails.getBrokerPort());
                        brokerConnectionType = initialMessageDetails.getConnectionType();

                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Received InitialPacket from broker with id : " + connectionBrokerInfo.getBrokerId());
                        if (initialMessageDetails.getConnectionType().equals(Constants.HEARTBEAT_CONNECTION)
                                && !membershipTable.isMember(connectionBrokerInfo.getBrokerId())) {
                            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] ConnectionType : "
                                    + initialMessageDetails.getConnectionType() +
                                    " BrokerId :" + connectionBrokerInfo.getBrokerId());
                            connectionBrokerInfo.setConnection(connection);
                            membershipTable.addMember(connectionBrokerInfo.getBrokerId(), connectionBrokerInfo);
                            heartBeatModule.updateHeartBeat(connectionBrokerInfo.getBrokerId());
                            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] ConnectionType : " + initialMessageDetails.getConnectionType() +
                                    " HeartBeat connection.Connection added to the list.");
                            try {
                                // send initial setup ack
                                connection.send(getInitialSetupACK(Constants.HEARTBEAT_CONNECTION));
                                //setting up dataConnection with this broker.Broker to send data.
                                Connection dataConnection = Utility.establishConnection(
                                        connectionBrokerInfo.getBrokerIP(),
                                        connectionBrokerInfo.getBrokerPort());
                                int retries = 0;
                                while (dataConnection == null && retries < Constants.MAX_RETRIES) {
                                    dataConnection = Utility.establishConnection(
                                            connectionBrokerInfo.getBrokerIP(),
                                            connectionBrokerInfo.getBrokerPort());
                                    retries++;
                                }
                                if (dataConnection != null) {
                                    if (sendInitialMessageToMember(dataConnection)) {
                                        membershipTable.addDataConnectionToMember(connectionBrokerInfo.getBrokerId(), dataConnection);
                                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] added data connection.");
                                    }
                                }
                            } catch (ConnectionClosedException e) {
                                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] " + e.getMessage());
                                connection.closeConnection();
                            }
                        } else if (initialMessageDetails.getConnectionType().equals(Constants.DATA_CONNECTION)) {
                            try {
                                // send initial setup ack
                                connection.send(getInitialSetupACK(Constants.DATA_CONNECTION));
                            } catch (ConnectionClosedException e) {
                                logger.info(e.getMessage());
                                connection.closeConnection();
                            }
                        } else if (initialMessageDetails.getConnectionType().equals(Constants.CATCHUP_CONNECTION)) {
                            try {
                                // send initial setup ack
                                connection.send(getInitialSetupACK(Constants.CATCHUP_CONNECTION));
                            } catch (ConnectionClosedException e) {
                                logger.info(e.getMessage());
                                connection.closeConnection();
                            }
                        }
                    }
                    name = initialMessageDetails.getName();
                } else {
                    try {
                        // send initial setup ack
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending Initial Setup ACK 2nd.");
                        connection.send(getInitialSetupACK(connectionWith));
                    } catch (ConnectionClosedException e) {
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] " + e.getMessage());
                        connection.closeConnection();
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred. Error Message : " + e.getMessage());
            }
        }
        return true;
    }

    /**
     *
     * @param connection
     */
    public boolean sendInitialMessageToMember(Connection connection) {
        boolean initialSetupDone = false;
        int messageID = 0;
        Any any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                .setMessageId(messageID)
                .setConnectionSender(Constants.BROKER)
                .setName(thisBrokerInfo.getBrokerName())
                .setBrokerId(thisBrokerInfo.getBrokerId())
                .setBrokerIP(thisBrokerInfo.getBrokerIP())
                .setBrokerPort(thisBrokerInfo.getBrokerPort())
                .setConnectionType(Constants.DATA_CONNECTION)
                .build());
        while (!initialSetupDone) {
            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Sending InitialSetup Message to the member.");
            try {
                connection.send(any.toByteArray());
                byte[] receivedMessage = connection.receive();
                if (receivedMessage != null) {
                    try {
                        Any any1 = Any.parseFrom(receivedMessage);
                        if (any1.is(InitialSetupDone.InitialSetupDoneDetails.class)) {
                            InitialSetupDone.InitialSetupDoneDetails initialSetupDoneDetails =
                                    any1.unpack(InitialSetupDone.InitialSetupDoneDetails.class);
                            initialSetupDone = true;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException while decoding Ack for InitialSetupMessage.");
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] " + e.getMessage());
                connection.closeConnection();
                break;
            }
        }
        return initialSetupDone;
    }

    /**
     * creates ack message for the initial setup message.
     * @return initialSetupACK byte array
     */
    public byte[] getInitialSetupACK(String connectionType) {
        Any any = null;
        if (connectionType.equals(Constants.CATCHUP_CONNECTION)) {
            any = Any.pack(InitialSetupDone.InitialSetupDoneDetails.newBuilder()
                    .setDone(true)
                    .addAllTopics(data.getTopicLists())
                    .build());
        } else {
            any = Any.pack(InitialSetupDone.InitialSetupDoneDetails.newBuilder()
                    .setDone(true)
                    .build());
        }
        return any.toByteArray();
    }

    /**
     * continuously receives publish message from publisher and add it to the topic.
     */
    public void handlePublisher() {
        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Handle producer.Producer. connection.Connection is open : " + connection.connectionIsOpen() +
                " LeaderId :" + membershipTable.getLeaderId() +
                " This broker.Broker Id : " + thisBrokerInfo.getBrokerId());
        while (connection.connectionIsOpen() &&
                membershipTable.getLeaderId() == thisBrokerInfo.getBrokerId()) {
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Handling Producer. connection.IsOpen : " + connection.connectionIsOpen());
            try {
                byte[] message = connection.receive();
                if (message != null) {
                    try {
                        Any any = Any.parseFrom(message);
                        if (any.is(PublisherPublishMessage.PublisherPublishMessageDetails.class)) {
                            PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails =
                                    any.unpack(PublisherPublishMessage.PublisherPublishMessageDetails.class);

                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Message Id : " + publisherPublishMessageDetails.getMessageId() +
                                    " Expected : " + messageId);
                            if (publisherPublishMessageDetails.getMessageId() == messageId && publisherPublishMessageDetails.getTopic() != null && publisherPublishMessageDetails.getMessage() != null) {
                                logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Receive Publish Request from " + name);
                                data.addMessageToTopic(Constants.SYNCHRONOUS, publisherPublishMessageDetails.getTopic(),
                                        publisherPublishMessageDetails.getMessage().toByteArray(), 0);
                                messageId++;
                            }
                            //send ack for the message is successfully published.
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending Ack with messageId : " + messageId);
                            Any any1 = Any.pack(PublishedMessageACK.PublishedMessageACKDetails.newBuilder()
                                    .setACKnum(messageId)
                                    .setTopic(publisherPublishMessageDetails.getTopic())
                                    .setStatus(true)
                                    .build());
                            connection.send(any1.toByteArray());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding publish message. Error Message : " + e.getMessage());
                    }
                } else {
                    synchronized (waitObj) {
                        startTimer();
                        try {
                            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Message is not received from " + name + " Waiting...");
                            waitObj.wait();
                        } catch (InterruptedException e) {
                            logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                        }
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                connection.closeConnection();
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePullConsumer() {
        while (connection.connectionIsOpen()) {
            try {
                byte[] message = connection.receive();
                if (message != null) {
                    try {
                        Any any = Any.parseFrom(message);
                        if (any.is(ConsumerPullRequest.ConsumerPullRequestDetails.class)) {
                            ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails =
                                    any.unpack(ConsumerPullRequest.ConsumerPullRequestDetails.class);
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Pull Request received. Topic : "
                                    + consumerPullRequestDetails.getTopic() +
                                    " offset : " + consumerPullRequestDetails.getOffset() + " messageId : " + consumerPullRequestDetails.getMessageId());
                            ArrayList<byte[]> messageBatch = null;
                            byte[] messageFromBroker;
                            // validating publish message
                            if (consumerPullRequestDetails.getTopic() != null) {
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Topic is available.");
                                messageBatch = data.getMessage(consumerPullRequestDetails.getTopic(),
                                        consumerPullRequestDetails.getOffset(), Constants.CONSUMER);
                                if (messageBatch != null) {
                                    messageFromBroker = createMessageFromBroker(consumerPullRequestDetails.getTopic(),
                                            messageBatch, Constants.MESSAGE, consumerPullRequestDetails.getMessageId());
                                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending prepared message batch of topic " + consumerPullRequestDetails.getTopic() + " to " + name);
                                } else {
                                    // message with given offset is not available
                                    messageFromBroker = createMessageFromBrokerInvalid(Constants.MESSAGE_NOT_AVAILABLE, consumerPullRequestDetails.getMessageId());
                                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending prepared message of type MESSAGE_NOT_AVAILABLE to "
                                            + name + " for request of topic " + consumerPullRequestDetails.getTopic());
                                }
                            } else {
                                messageFromBroker = createMessageFromBrokerInvalid(Constants.TOPIC_NOT_AVAILABLE, consumerPullRequestDetails.getMessageId());
                                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending message of type TOPIC_NOT_AVAILABLE to " + name +
                                        " for request of topic " + consumerPullRequestDetails.getTopic());
                            }
                            // sending response to the consumer
                            connection.send(messageFromBroker);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding pull request message " +
                                "from consumer. Error Message : " + e.getMessage());
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                connection.closeConnection();
            }

        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePushConsumer() {
        while (connection.connectionIsOpen()) {
            ArrayList<byte[]> messageBatch = null;
            messageBatch = data.getMessage(pushBasedConsumerTopic, offset.get(), Constants.CONSUMER);
            if (messageBatch != null) {
                byte[] messageFromBroker = createMessageFromBroker(pushBasedConsumerTopic,
                        messageBatch, Constants.MESSAGE, 0);
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending prepared message batch of topic " + pushBasedConsumerTopic + " to " + name);
                boolean sendSuccessful = false;
                try {
                    sendSuccessful = connection.send(messageFromBroker);
                } catch (ConnectionClosedException e) {
                    logger.info("\n" + e.getMessage());
                    connection.closeConnection();
                }
                if (sendSuccessful) {
                     for (byte[] eachMessage : messageBatch) {
                         offset.addAndGet(eachMessage.length); // updating offset value
                     }
                 }
            } else {
                synchronized (waitObj) {
                    startTimer();
                    try {
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Next Batch of message is not yet PUSHED to "
                                + brokerName + " fot the " + name + ". Waiting...");
                        waitObj.wait();
                    } catch (InterruptedException e) {
                        logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * method continuously listens for incoming message from broker
     * and takes action accordingly.
     */
    public void handleBroker() {
        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Handling broker.Broker. ConnectionType : " + brokerConnectionType);
        while (connection.connectionIsOpen()) {
            try {
                byte[] message = connection.receive();
                if (message != null) {
                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Message from " + connectionBrokerInfo.getBrokerName());
                    try {
                        Any any = Any.parseFrom(message);
                        if (any.is(HeartBeatMessage.HeartBeatMessageDetails.class)
                                && brokerConnectionType.equals(Constants.HEARTBEAT_CONNECTION)) {

                            HeartBeatMessage.HeartBeatMessageDetails HeartBeatMessageDetails =
                                    any.unpack(HeartBeatMessage.HeartBeatMessageDetails.class);
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Heart Beat Message from " + connectionBrokerInfo.getBrokerName());
                            // updating heartbeat message received time.
                            heartBeatModule.updateHeartBeat(connectionBrokerInfo.getBrokerId());

                        } else if (any.is(ElectionMessage.ElectionMessageDetails.class)
                                && brokerConnectionType.equals(Constants.HEARTBEAT_CONNECTION)) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Election Message from " + connectionBrokerInfo.getBrokerName() + " ID : " + connectionBrokerInfo.getBrokerId());
                            // updating heartbeat message received time.
                            heartBeatModule.updateHeartBeat(connectionBrokerInfo.getBrokerId());
                            Any electionResponseMessage = Any.pack(ElectionResponseMessage.ElectionResponseMessageDetails.newBuilder()
                                    .setMessageSenderId(thisBrokerInfo.getBrokerId()).build());
                            membershipTable.getMembershipInfo().get(connectionBrokerInfo.getBrokerId()).sendOverHeartbeatConnection(electionResponseMessage.toByteArray());
                            ElectionModule electionModule = ElectionModule.getElectionModule(thisBrokerInfo, data.getLoadBalancerIP(), data.getLoadBalancerPort());
                            if (!electionModule.getElectionStatus()) {
                                electionModule.setElectionStatus(true);
                                Thread electionThread = new Thread(){
                                    public void run(){
                                        electionModule.startElection();
                                    }
                                };
                                electionThread.start();
                            }
                        } else if (any.is(ElectionResponseMessage.ElectionResponseMessageDetails.class)
                                && brokerConnectionType.equals(Constants.HEARTBEAT_CONNECTION)) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Election Response Message from " + connectionBrokerInfo.getBrokerName() + " ID : " + connectionBrokerInfo.getBrokerId());
                            // updating heartbeat message received time.
                            heartBeatModule.updateHeartBeat(connectionBrokerInfo.getBrokerId());
                            ElectionModule electionModule = ElectionModule.getElectionModule(thisBrokerInfo, data.getLoadBalancerIP(), data.getLoadBalancerPort());
                            electionModule.setElectionResponseReceived(true); ////////////*TODO :
                            electionModule.notifyElectionResponseReceived();
                        } else if (any.is(VictoryMessage.VictoryMessageDetails.class)
                                && brokerConnectionType.equals(Constants.HEARTBEAT_CONNECTION)) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Victory Message from " + connectionBrokerInfo.getBrokerName() + " ID : " + connectionBrokerInfo.getBrokerId());
                            // updating heartbeat message received time.
                            heartBeatModule.updateHeartBeat(connectionBrokerInfo.getBrokerId());
                            ElectionModule electionModule = ElectionModule.getElectionModule(thisBrokerInfo, data.getLoadBalancerIP(), data.getLoadBalancerPort());
                            electionModule.setVictoryMessageReceived(true);
                            electionModule.notifyVictoryMessageReceived();
                            VictoryMessage.VictoryMessageDetails victoryMessageDetails = any.unpack(VictoryMessage.VictoryMessageDetails.class);
                            membershipTable.updateLeader(victoryMessageDetails.getNewLeaderId());
                        } else if (any.is(ReplicateMessage.ReplicateMessageDetails.class)
                                && brokerConnectionType.equals(Constants.DATA_CONNECTION)) {

                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "]Received Data over DataConnection type of connection.");
                            //wait to receive the message /data
                            ReplicateMessage.ReplicateMessageDetails replicateMessageDetails =
                                    any.unpack(ReplicateMessage.ReplicateMessageDetails.class);
                            data.addMessageToTopic(Constants.SYNCHRONOUS,
                                    replicateMessageDetails.getTopic(),
                                    replicateMessageDetails.getMessage().toByteArray(),
                                    replicateMessageDetails.getMessageId());
                            //send ack
                            long ackNum = replicateMessageDetails.getMessageId() + replicateMessageDetails.getMessage().size();
                            Any any1 = Any.pack(ReplicateSuccessACK.ReplicateSuccessACKDetails.newBuilder()
                                    .setAckNum(ackNum)
                                    .setTopic(replicateMessageDetails.getTopic())
                                    .build());
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] sending Ack for replication data.");
                            connection.send(any1.toByteArray());

                        } else if (any.is(SnapshotRequest.SnapshotRequestDetails.class)
                                && brokerConnectionType.equals(Constants.DATA_CONNECTION)) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Snapshot request over DataConnection type of connection.");
                            // send snapshot
                            DBSnapshot dbSnapshot = data.getSnapshot();
                            connection.send(Utility.getDBSnapshotMessage(dbSnapshot, thisBrokerInfo.getBrokerId(), Constants.DB_SNAPSHOT));
//                            membershipTable.getMembershipInfo().get(connectionBrokerInfo.getBrokerId())
//                                    .sendOverDataConnection(Utility.getDBSnapshotMessage(dbSnapshot, thisBrokerInfo.getBrokerId(), Constants.DB_SNAPSHOT));

                        } else if (any.is(CatchupPullRequest.CatchupPullRequestDetails.class)
                                && (brokerConnectionType.equals(Constants.CATCHUP_CONNECTION)
                                || brokerConnectionType.equals(Constants.DATA_CONNECTION))) {

                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received Data over CatchupConnection type of connection.");
                            //wait to receive the pull request.
                            CatchupPullRequest.CatchupPullRequestDetails catchupPullRequest =
                                    any.unpack(CatchupPullRequest.CatchupPullRequestDetails.class);
                            ArrayList<byte[]> messageBatch = null;
                            byte[] replicateMessage;
                            messageBatch = data.getMessage(catchupPullRequest.getTopic(),
                                    catchupPullRequest.getOffset(), Constants.BROKER);
                            if (messageBatch != null) {
                                replicateMessage = createReplicateData(
                                        catchupPullRequest.getTopic(),
                                        catchupPullRequest.getOffset(),
                                        messageBatch);
                                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending prepared message batch of topic " + catchupPullRequest.getTopic() + " to " + name);
                            } else {
                                // message with given offset is not available
                                replicateMessage = createReplicateData(catchupPullRequest.getTopic(),
                                        catchupPullRequest.getOffset(),
                                        null);
                                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] [SENDING] Sending prepared message of type MESSAGE_NOT_AVAILABLE to "
                                        + name + " for request of topic " + catchupPullRequest.getTopic());
                            }
                            connection.send(replicateMessage);
                        } else if (any.is(StartSyncUpMessage.StartSyncUpMessageDetails.class)
                                && brokerConnectionType.equals(Constants.DATA_CONNECTION)) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Received StartSyncUpMessage Data over DataConnection.");
                            data.setUpToDate(false);
                            StartSyncUpMessage.StartSyncUpMessageDetails startSyncUpMessageDetails =
                                    any.unpack(StartSyncUpMessage.StartSyncUpMessageDetails.class);
                            List<ByteString> topicSnapshotByteString = startSyncUpMessageDetails.getTopicSnapshotList();
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] topics list size : " + topicSnapshotByteString.size());
                            DBSnapshot leaderSnapshot = new DBSnapshot(startSyncUpMessageDetails.getMemberId());
                            for (ByteString eachTopicSnapshot : topicSnapshotByteString) {
                                Any eachTopicSnapshotAny = Any.parseFrom(eachTopicSnapshot);
                                if (eachTopicSnapshotAny.is(TopicSnapshot.TopicSnapshotDetails.class)) {
                                    TopicSnapshot.TopicSnapshotDetails topicSnapshotProto = eachTopicSnapshotAny.unpack(TopicSnapshot.TopicSnapshotDetails.class);
                                    model.TopicSnapshot topicSnapshotObj = new model.TopicSnapshot(topicSnapshotProto.getTopic(), topicSnapshotProto.getOffset());
                                    leaderSnapshot.addTopicSnapshot(topicSnapshotProto.getTopic(), topicSnapshotObj);
                                }
                            }
                            CatchupModule catchupModule = new CatchupModule(thisBrokerInfo.getBrokerName(),
                                    thisBrokerInfo, data.getLoadBalancerIP(), data.getLoadBalancerPort(), leaderSnapshot);
                            catchupModule.doSyncUpFollower();
                            data.setUpToDate(true);
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sync done after election");
//                            //send sync done message
//                            Any syncDoneMessage = Any.pack(StartSyncUpMessage.StartSyncUpMessageDetails.newBuilder().setMemberId(thisBrokerInfo.getBrokerId()).build());
//                            membershipTable.getMembershipInfo().get(connectionBrokerInfo.getBrokerId()).sendOverDataConnection(syncDoneMessage.toByteArray());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\n[ThreadId: " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding publish message. Error Message : " + e.getMessage());
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                //mark it down
                //connection.closeConnection();
            }
        }
    }

    /**
     * method takes ArrayList of message byteArray and topic
     * creates MessageFromBroker obj with it and returns its byteArray.
     * @param topic
     * @param messageBatch
     * @return
     */
    private byte[] createMessageFromBroker(String topic, ArrayList<byte[]> messageBatch, String type, int messageId) {
        ArrayList<ByteString> messageBatchStringArray = new ArrayList<>();
        for (byte[] eachMessage : messageBatch) {
            ByteString messageByteString = ByteString.copyFrom(eachMessage);
            messageBatchStringArray.add(messageByteString);
        }
        Any any = Any.pack(MessageFromBroker
                .MessageFromBrokerDetails.newBuilder()
                .setType(type)
                .setTopic(topic)
                .setTotalMessage(messageBatch.size())
                .addAllActualMessage(messageBatchStringArray)
                .setMessageId(messageId)
                .build());
        return any.toByteArray();
    }

    /**
     * method takes ArrayList of message byteArray and topic
     * creates MessageFromBroker obj with it and returns its byteArray.
     * @param topic
     * @param messageBatch
     * @return
     */
    private byte[] createReplicateData(String topic, long messageId, ArrayList<byte[]> messageBatch) {
        Any any = null;
        if (messageBatch != null) {
            ArrayList<ByteString> messageBatchStringArray = new ArrayList<>();
            for (byte[] eachMessage : messageBatch) {
                ByteString messageByteString = ByteString.copyFrom(eachMessage);
                messageBatchStringArray.add(messageByteString);
            }
            any = Any.pack(ReplicateMessage
                    .ReplicateMessageDetails.newBuilder()
                    .setSynchronous(false)
                    .setMessageId(messageId)
                    .setTopic(topic)
                    .setTotalMessage(messageBatch.size())
                    .addAllMessageBatch(messageBatchStringArray)
                    .build());
        } else {
            any = Any.pack(ReplicateMessage
                    .ReplicateMessageDetails.newBuilder()
                    .setSynchronous(false)
                    .setMessageId(messageId)
                    .setTopic(topic)
                    .setTotalMessage(0)
                    .build());
        }
        return any.toByteArray();
    }

    /**
     * method creates MessageFromBroker obj of type INVALID
     * and returns its byteArray.
     * @return byte[]
     */
    public byte[] createMessageFromBrokerInvalid(String type, int messageId) {
        Any any = Any.pack(MessageFromBroker.
                MessageFromBrokerDetails.newBuilder()
                .setType(type)
                .setMessageId(messageId)
                .build());
        return any.toByteArray();
    }

}
