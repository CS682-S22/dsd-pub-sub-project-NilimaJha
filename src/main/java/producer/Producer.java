package producer;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import customeException.ConnectionClosedException;
import util.Constants;
import model.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;

/**
 * Class extends Node class and is a producer
 * @author nilimajha
 */
public class Producer extends Node {
    private static final Logger logger = LogManager.getLogger(Producer.class);
    private volatile int messageId = 0;
    private final Object connectBrokerWaitObj = new Object();

    /**
     * constructor for producer class attributes
     * @param producerName
     * @param loadBalancerName
     * @param loadBalancerIP
     * @param loadBalancerPort
     */
    public Producer (String producerName, String loadBalancerName, String loadBalancerIP, int loadBalancerPort) {
        super(producerName, Constants.PRODUCER, loadBalancerName, loadBalancerIP, loadBalancerPort);
        setupConnectionWithLeaderBroker();
    }

    /**
     * constructor mainly for test for producer class attributes
     * @param producerName
     * @param loadBalancerName
     * @param loadBalancerIP
     * @param loadBalancerPort
     * @param test
     */
    public Producer(String producerName, String loadBalancerName, String loadBalancerIP, int loadBalancerPort, String test) {
        super(producerName, Constants.PRODUCER, loadBalancerName, loadBalancerIP, loadBalancerPort);
    }

    /**
     * method connects with the loadBalance and gets the leader broker information
     * and sets up connection with leader broker.
     */
    public void setupConnectionWithLeaderBroker() {
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Inside startProducer : connected : " + connected);
        while (!connected) {
            resetLeaderBrokerInfo();
            resetMessageId();
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Not Connected with broker.");
            connectBroker();
        }
    }

    /**
     * connects to the broker and then sets itself up by sending InitialPacket.
     */
    public boolean connectBroker() {
        getLeadersInfo();
        int retries = 0;
        while (!connected && retries < Constants.MAX_RETRIES) {
            try {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Trying to connect leader broker. Name :" + leaderBrokerName + " IP :" + leaderBrokerIP + " Port :" + leaderBrokerPort);
                connectToBroker();
            } catch (ConnectionClosedException e) {
                retries++;
                logger.info(e.getMessage());
                synchronized (connectBrokerWaitObj) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Waiting for sometime before retrying.");
                    try {
                        connectBrokerWaitObj.wait(Constants.RETRIES_TIMEOUT);
                    } catch (InterruptedException ex) {
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred while waiting before reconnecting to broker. Error Message : " + e.getMessage());
                    }
                }
            }
        }
        if (connected) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Sending InitialSetupMessage.");
            sendInitialSetupMessage();
            return true;
        }
        return false;
    }

    /**
     * connects to loadBalancer and gets leader's information
     */
    public void getLeadersInfo() {
        try {
            resetLeaderBrokerInfo();
            connectToLoadBalancer();
            while (leaderBrokerIP == null) {
                getLeaderAndMembersInfo();
            }
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] LeaderBrokerIp : " + leaderBrokerIP + "leaderPort: " + leaderBrokerPort);
            closeLoadBalancerConnection();
        } catch (ConnectionClosedException e) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Exception occurred while connecting to loadBalancer.LoadBalancer. Error Message : " + e.getMessage());
            System.exit(0);
        }

    }

    /**
     * creates and sends Initial Packet to the Broker.
     */
    public boolean sendInitialSetupMessage() {
        boolean initialSetupDone = false;
        int messageID = 0;
        byte[] initialMessagePacket = createInitialMessagePacket1(messageID);
        while (!initialSetupDone) {
            try {
                //send initial message
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [Sending Initial packet]");
                connection.send(initialMessagePacket); //sending initial packet
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
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException while decoding Ack for InitialSetupMessage.");
                    }
                }
            } catch (ConnectionClosedException e) {
                connected = false;
                connection.closeConnection();
                connection = null;
                return false;
            }
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InitialSetupDone : " + initialSetupDone);
        }
        return true;
    }

    /**
     * creates the Producer Initial packet.
     * @return byte[] array
     */
    public byte[] createInitialMessagePacket1(int messageId) {
        Any any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                .setMessageId(messageId)
                .setConnectionSender(Constants.PRODUCER)
                .setName(name)
                .setNextMessageId(messageId)
                .build());
        return any.toByteArray();
    }

    /**
     * method creates the publishMessage packet with the given message and topic.
     * @param topic to which the data belongs
     * @param data actual data to be published.
     * @return
     */
    public byte[] createPublishMessagePacket(String topic, byte[] data) {
        Any any = Any.pack(PublisherPublishMessage
                .PublisherPublishMessageDetails.newBuilder()
                .setMessageId(messageId)
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build());
        return any.toByteArray();
    }

    /**
     * it takes the message to be published on the broker and
     * also the topic to which this message will be published on broker and
     * sends over the connection established with the broker.
     * @param topic topic of the data
     * @param data actual data to be published
     * @return true/false
     */
    public boolean send (String topic, byte[] data) {
        boolean sent = false;
        while (!sent) {
            if (connected) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [SEND] Publishing Message on Topic " + topic);
            } else {
                setupConnectionWithLeaderBroker();
            }
            sent = sendEachMessage(topic, data);
            messageId++;
        }
        return true;
    }

    /**
     * sends message to the currentLeaderBroker and if LeaderBroker fails then
     * connect to the new leader by getting info from loadBalancer.LoadBalancer.
     * @param topic
     * @param data
     * @return
     */
    private boolean sendEachMessage(String topic, byte[] data) {

        boolean sentSuccess = false;
        while (!sentSuccess) {
            if (connection != null && connected && connection.connectionIsOpen()) {
                try {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [SEND] Publishing Message on Topic " + topic);
                    connection.send(createPublishMessagePacket(topic, data));
                    byte[] receivedAck = connection.receive();
                    if (receivedAck != null) {
                        try {
                            Any any = Any.parseFrom(receivedAck);
                            if (any.is(PublishedMessageACK.PublishedMessageACKDetails.class)) {
                                PublishedMessageACK.PublishedMessageACKDetails publishedMessageACKDetails =
                                        any.unpack(PublishedMessageACK.PublishedMessageACKDetails.class);
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] MessageId : " + publishedMessageACKDetails.getACKnum());
                                if (publishedMessageACKDetails.getACKnum() == messageId + 1) {
                                    sentSuccess = true;
                                    break;
                                }
                            }
                        } catch (InvalidProtocolBufferException e) {
                            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding Ack message for Published message. Error Message : " + e.getMessage());
                        }
                    }
                } catch (ConnectionClosedException e) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] " + e.getMessage());
                    connection.closeConnection();
                    connected = false;
                    setupConnectionWithLeaderBroker();
                }
            } else {
                connected = false;
                setupConnectionWithLeaderBroker();
            }
        }
        return sentSuccess;
    }

    /**
     * closes the connection.
     */
    public void close() {
        boolean closeSuccessful = false;
        while (!closeSuccessful) {
            closeSuccessful = connection.closeConnection();
        }
    }

    /**
     * resets attribute messageId to 0.
     * @return true
     */
    public boolean resetMessageId() {
        messageId = 0;
        return true;
    }

    /**
     * return the status of the connection with broker.
     * @return
     */
    public boolean isConnected() {
        return connected;
    }
}
