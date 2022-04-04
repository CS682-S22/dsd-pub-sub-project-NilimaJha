import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import model.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * class to handle connection between broker and producer or consumer.
 * @author nilimajha
 */
public class RequestProcessor implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    private String brokerName;
    private Connection connection;
    private Data data;
    private String connectionWith;
    private String consumerType;
    private String name;
    private AtomicLong offset = new AtomicLong(-1);
    private String pushBasedConsumerTopic = null;

    /**
     * Constructor that initialises Connection class object and also model.Data
     * @param connection
     */
    public RequestProcessor(String brokerName, Connection connection) {
        this.brokerName = brokerName;
        this.connection = connection;
        this.data = Data.getData();
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
        while (connectionWith == null) {
            byte[] receivedMessage = connection.receive(); // getting initial setup Message.
            if (receivedMessage != null) { // received initial message
                // call decode packet and then call decode message inside
                try {
                    Any any = Any.parseFrom(receivedMessage);
                    if (any.is(InitialMessage.InitialMessageDetails.class)) {
                        parseInitialMessage1(any);
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error("\nInvalidProtocolBufferException occurred decoding Initial Packet. Error Message : " + e.getMessage());
                }
            }
        }

        if (connectionWith.equals(Constants.PRODUCER)) {
            handlePublisher();
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PULL)) {
            handlePullConsumer();
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PUSH)) {
            handlePushConsumer();
        }
    }

    /**
     * decode message field of the PacketDetails object as per the type.
     * @param any
     */
    public boolean parseInitialMessage1(Any any) {
        if (any.is(InitialMessage.InitialMessageDetails.class)) {
            // decode received message
            try {
                InitialMessage.InitialMessageDetails initialMessageDetails =
                        any.unpack(InitialMessage.InitialMessageDetails.class);
                if (connectionWith == null) {
                    if (initialMessageDetails.getConnectionSender().equals(Constants.PRODUCER)) {
                        connectionWith = Constants.PRODUCER;
                        logger.info("\nReceived InitialPacket from " + initialMessageDetails.getName());
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) &&
                            initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PULL)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PULL;    // PULL consumer
                        logger.info("\nReceived InitialPacket from " + initialMessageDetails.getName() +
                                " Consumer Type : " + consumerType);
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) &&
                            initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PUSH)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PUSH;    // PUSH consumer
                        offset.set(initialMessageDetails.getInitialOffset());
                        pushBasedConsumerTopic = initialMessageDetails.getTopic();
                        logger.info("\nReceived InitialPacket from " + initialMessageDetails.getName() +
                                " Consumer Type : " + consumerType + ", InitialOffset : " + offset +
                                ", Topic : " + pushBasedConsumerTopic);
                    }
                    name = initialMessageDetails.getName();
                }
            } catch (InvalidProtocolBufferException e) {
                logger.error("\nInvalidProtocolBufferException occurred decoding Initial Packet. Error Message : " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * continuously receives publish message from publisher and add it to the topic.
     */
    public void handlePublisher() {
        while (connection.connectionIsOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                try {
                    Any any = Any.parseFrom(message);
                    if (any.is(PublisherPublishMessage.PublisherPublishMessageDetails.class)) {
                        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails =
                                any.unpack(PublisherPublishMessage.PublisherPublishMessageDetails.class);

                        if (publisherPublishMessageDetails.getTopic() != null && publisherPublishMessageDetails.getMessage() != null) {
                            logger.info("\nReceive Publish Request from " + name);
                            data.addMessage(publisherPublishMessageDetails.getTopic(),
                                    publisherPublishMessageDetails.getMessage().toByteArray());
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error("\nInvalidProtocolBufferException occurred while decoding publish message. Error Message : " + e.getMessage());
                }
            } else {
                try {
                    logger.info("\nMessage is not received from " + name);
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    logger.error("\nInterruptedException occurred. Error Message : " + e.getMessage());
                }
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePullConsumer() {
        while (connection.connectionIsOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                try {
                    Any any = Any.parseFrom(message);
                    if (any.is(ConsumerPullRequest.ConsumerPullRequestDetails.class)) {
                        ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails =
                                any.unpack(ConsumerPullRequest.ConsumerPullRequestDetails.class);
                        ArrayList<byte[]> messageBatch = null;
                        byte[] messageFromBroker;
                        // validating publish message
                        if (consumerPullRequestDetails.getTopic() != null) {
                            messageBatch = data.getMessage(consumerPullRequestDetails.getTopic(),
                                    consumerPullRequestDetails.getOffset());
                            if (messageBatch != null) {
                                messageFromBroker = createMessageFromBroker(consumerPullRequestDetails.getTopic(),
                                        messageBatch, Constants.MESSAGE);
                                logger.info("\n[SENDING] Sending prepared message batch of topic " + consumerPullRequestDetails.getTopic() + " to " + name);
                            } else {
                                // message with given offset is not available
                                messageFromBroker = createMessageFromBrokerInvalid(Constants.MESSAGE_NOT_AVAILABLE);
                                logger.info("\n[SENDING] Sending prepared message of type MESSAGE_NOT_AVAILABLE to "
                                        + name + " for request of topic " + consumerPullRequestDetails.getTopic());
                            }
                        } else {
                            messageFromBroker = createMessageFromBrokerInvalid(Constants.TOPIC_NOT_AVAILABLE);
                            logger.info("\n[SENDING] Sending message of type TOPIC_NOT_AVAILABLE to " + name +
                                    " for request of topic " + consumerPullRequestDetails.getTopic());
                        }
                        // sending response to the consumer
                        connection.send(messageFromBroker);
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.info("\nInvalidProtocolBufferException occurred while decoding pull request message " +
                            "from consumer. Error Message : " + e.getMessage());
                }
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
            messageBatch = data.getMessage(pushBasedConsumerTopic, offset.get());
            if (messageBatch != null) {
                byte[] messageFromBroker = createMessageFromBroker(pushBasedConsumerTopic,
                        messageBatch, Constants.MESSAGE);
                logger.info("\n[SENDING] Sending prepared message batch of topic " + pushBasedConsumerTopic + " to " + name);
                boolean sendSuccessful = connection.send(messageFromBroker);
                 if (sendSuccessful) {

                     for (byte[] eachMessage : messageBatch) {
                         offset.addAndGet(eachMessage.length); // updating offset value
                     }
                 }
            } else {
                try {
                    logger.info("\nNext Batch of message is not yet PUSHED to " + brokerName + " fot the " + name);
                    Thread.sleep(500); //sleeping for 500 millisecond
                } catch (InterruptedException e) {
                    logger.error("\nInterruptedException occurred. Error Message : " + e.getMessage());
                }
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
    private byte[] createMessageFromBroker(String topic, ArrayList<byte[]> messageBatch, String type) {
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
                .build());
        return any.toByteArray();
    }

    /**
     * method creates MessageFromBroker obj of type INVALID
     * and returns its byteArray.
     * @return byte[]
     */
    public byte[] createMessageFromBrokerInvalid(String type) {
        Any any = Any.pack(MessageFromBroker.
                MessageFromBrokerDetails.newBuilder()
                .setType(type)
                .build());
        return any.toByteArray();
    }
}
