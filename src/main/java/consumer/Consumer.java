package consumer;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import customeException.ConnectionClosedException;
import proto.InitialSetupDone;
import util.Constants;
import model.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.ConsumerPullRequest;
import proto.InitialMessage;
import proto.MessageFromBroker;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Class extends Node class and is a Consumer
 * @author nilimajha
 */
public class Consumer extends Node {
    private static final Logger logger = LogManager.getLogger(Consumer.class);
    private String consumerType;
    private AtomicLong offset;
    private String topic;
    private BlockingQueue<byte[]> messageFromBroker;
    private boolean initialSetupDone;
    private boolean shutdown = false;
    private Timer timer;
    private final Object getBrokerInfoFromLBWaitObj = new Object();

    /**
     * Constructor initialises the class attributes and
     * also starts consumer to receive the data from broker.
     * @param consumerName consumer name
     * @param consumerType consumer type
     * @param loadBalancerName name of loadBalancer
     * @param loadBalancerIP broker Ip
     * @param loadBalancerPort broker.Broker port
     * @param topic topic from which this consumer will get data
     * @param startingPosition offset from which the consumer will start pulling data.
     */
    public Consumer(String consumerName, String consumerType, String loadBalancerName, String loadBalancerIP,
                    int loadBalancerPort, String topic, long startingPosition) {
        super(consumerName, Constants.CONSUMER, loadBalancerName, loadBalancerIP, loadBalancerPort);
        this.consumerType = consumerType;
        this.topic = topic;
        this.offset = new AtomicLong(startingPosition);
        this.messageFromBroker = new LinkedBlockingQueue<>();
        setUpBrokerConnection();
        startTimer();
    }

    /**
     * method starts timerTask which calls method that performs pulling the data from broker.
     */
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                startPullingData();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, Constants.TIMEOUT_IF_DATA_NOT_YET_AVAILABLE);
    }

    /**
     * method makes sure that the connection with broker is established.
     */
    public void setUpBrokerConnection() {
        while (!connected) {
            try {
                if (loadBalancerConnection == null || !loadBalancerConnection.isConnected()) {
                    connectToLoadBalancer();
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] LoadBalancer Is not running on the given IP and port. Exiting the system.");
                System.exit(0);
            }
            try {
                getLeaderAndMembersInfo();
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Trying to connect to broker. Name :" + leaderBrokerName + " IP :" + leaderBrokerIP + " Port :" + leaderBrokerPort);
                connectToBroker();
                if (connected) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Sending InitialSetupMessage.");
                    getInitialSetupDone();
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Closing loadBalancerConnection.");
                    loadBalancerConnection.closeConnection();
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Broker is not available at given port. Waiting for some time then Getting broker info from LoadBalancer.");
                synchronized (getBrokerInfoFromLBWaitObj) {
                    try {
                        getBrokerInfoFromLBWaitObj.wait(Constants.RETRIES_TIMEOUT);
                    } catch (InterruptedException ex) {
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * this method pulls data from broker if connected else sets up the connection.
     */
    public void startPullingData() {
        timer.cancel();
        while (!shutdown) {
            if (connected) {
                try {
                    boolean messageAvailable = pullMessageFromBroker1();
                    if (!messageAvailable) {
                        startTimer();
                        break;
                    }
                } catch (ConnectionClosedException e) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "]" + e.getMessage());
                }
            } else  {
                setUpBrokerConnection();
            }

        }
    }

    /**
     * method sends the pull request to the broker and then waits to receive the message.
     * @return true/false
     * @throws ConnectionClosedException
     */
    public boolean pullMessageFromBroker1() throws ConnectionClosedException {
        byte[] requestMessagePacket = createPullRequestMessagePacket();
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [SEND] Sending pull request to Broker " + leaderBrokerName + " for Offset " + offset.get());
        boolean success = false;
        try {
            connection.send(requestMessagePacket); // sending pull request to the broker
            byte[] messageReceived = null;
            while (messageReceived == null) {
                messageReceived = connection.receive();
//                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Waiting to receive response");
                if (messageReceived != null) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received response...");
                    success = extractDataFromBrokerResponse1(messageReceived);
                }
            }
        } catch (ConnectionClosedException e) {
//            logger.error(e.getMessage());
            connection.closeConnection();
            connected = false;
            throw new ConnectionClosedException("\nConnection with broker is closed.");
        }
        return success;
    }

    /**
     * method decodes the message received from the broker and pulls actual message.
     * if the actual message is received then adds it into the queue.
     * @param messageReceived
     * @return true/false
     */
    public boolean extractDataFromBrokerResponse1(byte[] messageReceived) {
        boolean success = false;
//        logger.info("\nmessageReceived.");
        if (messageReceived != null) {
            try {
//                logger.info("\nmessageReceived2.");
                Any any = Any.parseFrom(messageReceived);
//                logger.info("\nany is of type InitialSetupDone? " + any.is(InitialSetupDone.InitialSetupDoneDetails.class));
                if (any.is(MessageFromBroker.MessageFromBrokerDetails.class)) {
//                    logger.info("\nmessageReceived.3");
                    MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails =
                            any.unpack(MessageFromBroker.MessageFromBrokerDetails.class);
//                    logger.info("\nmessageReceived.4");
                    if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE)) {
//                        logger.info("\nmessageReceived.5a");
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Total message received from broker in one response = "
                                + messageFromBrokerDetails.getActualMessageCount());
                        for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                            byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                            try {
                                messageFromBroker.put(actualMessageBytes);
                            } catch (InterruptedException e) {
                                logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred while trying to put new message into list. Error Message : " + e.getMessage());
                            }
                            if (consumerType.equals(Constants.CONSUMER_PULL)) {
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] PullConsumer incrementing offset by : " + actualMessageBytes.length);
                                offset.addAndGet(actualMessageBytes.length); // incrementing offset value to the next message offset
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Incremented offset : " + offset.get());
                            }
                        }
                        success = true;
                    } else {
//                        logger.info("\nmessageReceived.5b");
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Message type : " + messageFromBrokerDetails.getType());
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding message from broker.Broker. Error Message : " + e.getMessage());
            }
        }
        return success;
    }

    /**
     * method sends Consumer Initial setup packet to the broker.
     */
    public boolean getInitialSetupDone() {
        boolean initialSetupDone = false;
        //send initial message
        byte[] initialMessagePacket = createInitialMessagePacket();
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [Sending Initial packet].");
        //sending initial packet
        try {
            connection.send(initialMessagePacket);
            while (!initialSetupDone) {
                byte[] initialSetupDoneAck = connection.receive();
                if (initialSetupDoneAck != null) {
                    Any any = Any.parseFrom(initialSetupDoneAck);
                    if (any.is(InitialSetupDone.InitialSetupDoneDetails.class)) {
                        InitialSetupDone.InitialSetupDoneDetails initialSetupDoneDetails
                                = any.unpack(InitialSetupDone.InitialSetupDoneDetails.class);
                        initialSetupDone = true;
                    }
                }
            }
        } catch (ConnectionClosedException e) {
            logger.info(e.getMessage());
            connection.closeConnection();
            connected = false;
            return false;
        } catch (InvalidProtocolBufferException e) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException Occurred. Error Message : " + e.getMessage());
        }
        return initialSetupDone;
    }

    /**
     * method creates appropriate Initial message for the broker as per the consumer type.
     * @return initialMessagePacketByteArray
     */
    public byte[] createInitialMessagePacket() {
        Any any;
        if (consumerType.equals(Constants.CONSUMER_PULL)) {
            any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionSender(Constants.CONSUMER)
                    .setName(name)
                    .setConsumerType(Constants.CONSUMER_PULL)
                    .build());
        } else {
            any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionSender(Constants.CONSUMER)
                    .setName(name)
                    .setConsumerType(Constants.CONSUMER_PUSH)
                    .setTopic(topic)
                    .setInitialOffset(offset.get())
                    .build());
        }
        return any.toByteArray();
    }

    /**
     * method creates pull request message.
     * @return byte[]
     */
    public byte[] createPullRequestMessagePacket() {
        Any any = Any.pack(ConsumerPullRequest
                .ConsumerPullRequestDetails.newBuilder()
                .setTopic(topic)
                .setOffset(offset.get())
                .build());
        return any.toByteArray();
    }

    /**
     * return application program the byte array of message fetched from broker.
     * @return message
     * @param duration maximum time to wait if data is not yet available.
     */
    public byte[] poll(Duration duration)  {
        byte[] message = null;
        try {
            message = messageFromBroker.poll(duration.toMillis(), MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred while trying to poll message from consumer. Error Message : " + e.getMessage());
        }
        return message;
    }

    /**
     * getter for offset
     * @return offset
     */
    public long getOffset() {
        return offset.get();
    }

    /**
     * getter for topic
     * @return topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * return the status of flag shutdown.
     */
    public boolean isShutdown() {
        return shutdown;
    }
}
