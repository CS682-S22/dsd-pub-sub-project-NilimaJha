import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
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
    private Timer timer;

    /**
     * Constructor initialises the class attributes and
     * also starts consumer to receive the data from broker.
     * @param consumerName consumer name
     * @param consumerType consumer type
     * @param brokerIP broker Ip
     * @param brokerPort Broker port
     * @param topic topic from which this consumer will get data
     * @param startingPosition offset from which the consumer will start pulling data.
     */
    public Consumer(String consumerName, String consumerType, String brokerIP,
                    int brokerPort, String topic, long startingPosition) {

        super(consumerName, brokerIP, brokerPort);
        this.consumerType = consumerType;
        this.topic = topic;
        this.offset = new AtomicLong(startingPosition);
        this.messageFromBroker = new LinkedBlockingQueue<>();
        connectToBroker();
        startTimer();
    }

    /**
     *
     */
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                startConsumer();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, Constants.TIMEOUT_IF_DATA_NOT_YET_AVAILABLE);
    }



    /**
     * first connects to the broker and
     * sets itself up with Broker by sending InitialPacket.
     * then after receive data from broker by
     * calling appropriate function as per the time of consumer
     */
    public void startConsumer() {
        timer.cancel();
        if (!connected) {
            connectToBroker();
        }

        if (connected) {
            if (!initialSetupDone) {
                initialSetupDone = sendInitialSetupMessage();
            }
            if (initialSetupDone) {
                if (consumerType.equals(Constants.CONSUMER_PULL)) {
                    while (connection.connectionIsOpen()) {
                        logger.info("\n[Pulling message from broker]");
                        boolean topicIsAvailable = pullMessageFromBroker(); // fetching data from broker
                        if (!topicIsAvailable) {
                            startTimer();
                            break;
                        }
                    }
                } else {
                    while (connection.connectionIsOpen()) {
                        receiveMessageFromBroker(); // receiving data from broker
                    }
                }
            }
        }
    }

    /**
     * method sends Consumer Initial setup packet to the broker.
     */
    public boolean sendInitialSetupMessage() {
        //send initial message
        byte[] initialMessagePacket = createInitialMessagePacket();
        logger.info("\n[Sending Initial packet].");
        //sending initial packet
        return connection.send(initialMessagePacket);
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
     * method pulls message from broker
     * at first it sends pull message to the broker
     * and then receives message sent by broker.
     */
    public boolean pullMessageFromBroker() {
        byte[] requestMessagePacket = createPullRequestMessagePacket();
        logger.info("\n[SEND] Sending pull request to Broker for Offset " + offset.get());
        connection.send(requestMessagePacket); // sending pull request to the broker
        return receiveMessageFromBroker();
    }

    /**
     * method receive message from broker.
     */
    private boolean receiveMessageFromBroker() {
        boolean successful = true;
        byte[] brokerMessage = connection.receive();
        if (brokerMessage != null) {
            logger.info("\n[RECEIVE] Received Response from Broker.");
            successful = extractDataFromBrokerResponse(brokerMessage);
        }
        return successful;
    }

    /**
     * method extracts data from message received from broker.
     * @param brokerMessage message received from broker
     * @return true/false
     */
    private boolean extractDataFromBrokerResponse(byte[] brokerMessage) {
        boolean success = false;
        if (brokerMessage != null) {
            try {
                Any any = Any.parseFrom(brokerMessage);
                if (any.is(MessageFromBroker.MessageFromBrokerDetails.class)) {
                    MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails =
                            any.unpack(MessageFromBroker.MessageFromBrokerDetails.class);
                    if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE)) {
                        logger.info("\n[RECEIVE] Total message received from broker in one response = " + messageFromBrokerDetails.getActualMessageCount());
                        for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                            byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                            try {
                                messageFromBroker.put(actualMessageBytes);
                            } catch (InterruptedException e) {
                                logger.error("\nInterruptedException occurred while trying to put new message into list. Error Message : " + e.getMessage());
                            }
                            if (consumerType.equals(model.Constants.CONSUMER_PULL)) {
                                offset.addAndGet(actualMessageBytes.length); // incrementing offset value to the next message offset
                            }
                        }
                        success = true;
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                logger.info("\nInvalidProtocolBufferException occurred while decoding message from Broker. Error Message : " + e.getMessage());
            }
        }
        return success;
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
            logger.error("\nInterruptedException occurred while trying to poll message from consumer. Error Message : " + e.getMessage());
        }
        return message;
    }

    /**
     * getter for consumerType
     * @return consumerType
     */
    public String getConsumerType() {
        return consumerType;
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
}
