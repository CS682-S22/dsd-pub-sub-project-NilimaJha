import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import model.Data;
import model.DataInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import proto.*;

import java.util.ArrayList;

/**
 * class to handle connection between broker and producer or consumer.
 * @author nilimajha
 */
public class RequestProcessor implements Runnable {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(RequestProcessor.class);
    private Connection connection;
    private Data data;
    private String connectionWith;
    private String consumerType;
    private String name;
    private int offset = -1;
    private String pushBasedConsumerTopic = null;

    /**
     * Constructor that initialises Connection class object and also model.Data
     * @param connection
     */
    public RequestProcessor(Connection connection) {
        this.connection = connection;
        this.data = DataInitializer.getData();
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
                Packet.PacketDetails packetDetails = null;
                try {
                    packetDetails = Packet.PacketDetails.parseFrom(receivedMessage);
                } catch (InvalidProtocolBufferException e) {
                    LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InvalidProtocolBufferException : " + e);
                }
                LOGGER.info("\n[Thread Id : " + Thread.currentThread().getId() + "] [Received InitialPacket] Sender name: " + packetDetails.getFrom());
                System.out.printf("\n[Thread Id : %s] [Received InitialPacket]\n", Thread.currentThread().getId());
                parseInitialMessage(packetDetails);

                if (connectionWith.equals(Constants.PRODUCER)) {
                    LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [INITIAL SETUP DONE] Connection with : " + connectionWith + ", Connection name : " + name);
                    System.out.printf("\n[Thread Id : %s] [INITIAL SETUP DONE] Connection with : %s, Connection name : %s\n", Thread.currentThread().getId(), connectionWith, name);
                } else {
                    LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [INITIAL SETUP DONE] Connection with : " + connectionWith + ", Connection name : " + name + ", Consumer Type : " + consumerType);
                    System.out.printf("\n[Thread Id : %s] [INITIAL SETUP DONE] Connection with : %s, Connection name : %s, Consumer Type : %s\n", Thread.currentThread().getId(), connectionWith, name, consumerType);
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
     * @param packetDetails
     */
    public boolean parseInitialMessage(Packet.PacketDetails packetDetails) {
        if (packetDetails.getType().equals(Constants.INITIAL)) {
            LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [Parsing Initial Message]");
            // decode message part from packetDetail to InitialMessage proto
            try {
                InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage());
                if (connectionWith == null) {
                    if (initialMessageDetails.getConnectionSender().equals(Constants.PRODUCER)) {
                        connectionWith = Constants.PRODUCER;
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) && initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PULL)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PULL;    // type of consumer
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) && initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PUSH)) {
                        connectionWith = Constants.CONSUMER;
                        consumerType = Constants.CONSUMER_PUSH;    // type of consumer
                        offset = initialMessageDetails.getInitialOffset();
                        pushBasedConsumerTopic = initialMessageDetails.getTopic();
                    }
                    name = initialMessageDetails.getName();
                }
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InvalidProtocolBufferException : " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * receives publish message from publisher and add it to the topic.
     */
    public void handlePublisher() {
        while (connection.connectionSocket.isOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                try {
                    PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.parseFrom(message);
                    if (publisherPublishMessageDetails.getTopic() != null && publisherPublishMessageDetails.getMessage() != null) { // validating publish message
                        LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [" + name + " produced a message on topic " + publisherPublishMessageDetails.getTopic() + "]");
                        data.addMessage(publisherPublishMessageDetails.getTopic(), publisherPublishMessageDetails.getMessage().toByteArray());
                    }
                    // if publish message is not valid then doing nothing.
                } catch (InvalidProtocolBufferException e) {
                    LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InvalidProtocolBufferException : " + e);
                }
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePullConsumer() {
        while (connection.connectionSocket.isOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                try {
                    ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.parseFrom(message);
                    ArrayList<byte[]> messageBatch = null;
                    byte[] finalByteArray;
                    if (consumerPullRequestDetails.getTopic() != null) { // validating publish message
                        LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [RECEIVED] [" + name + " pull request for message from offset " + consumerPullRequestDetails.getOffset() + " of topic " + consumerPullRequestDetails.getTopic() + "]");
                        System.out.printf("\n[Thread Id : %s] [RECEIVED] [%s pull request for message from offset %d of topic %s]\n", Thread.currentThread().getId(), name, consumerPullRequestDetails.getOffset(), consumerPullRequestDetails.getTopic());
                        messageBatch = data.getMessage(consumerPullRequestDetails.getTopic(), consumerPullRequestDetails.getOffset());
                        LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] Getting message from offset '" + consumerPullRequestDetails.getOffset() + "' from file.");
                        if (messageBatch != null) {
                            finalByteArray = createMessageFromBroker(consumerPullRequestDetails.getTopic(), messageBatch);
                        } else {
                            // message with given offset is not available
                            finalByteArray = createMessageFromBrokerInvalid(Constants.MESSAGE_NOT_AVAILABLE);
                        }
                    } else {
                        finalByteArray = createMessageFromBrokerInvalid(Constants.TOPIC_NOT_AVAILABLE);
                    }
                    // sending response to the consumer
                    LOGGER.info("[THREAD ID : " + Thread.currentThread().getId() + "] [SENDING] [Sending response to " + name + "]");
                    System.out.printf("\n[THREAD ID : %s] [SENDING] [Sending response to %s.]\n", Thread.currentThread().getId(), name);
                    connection.send(finalByteArray);
                } catch (InvalidProtocolBufferException e) {
                    LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InvalidProtocolBufferException : " + e);
                }
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePushConsumer() {
        while (connection.connectionSocket.isOpen()) {
            ArrayList<byte[]> messageBatch = null;
            LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] Getting message from offset '" + offset + "' from file.");
            messageBatch = data.getMessage(pushBasedConsumerTopic, offset);
            if (messageBatch != null) {
                byte[] finalMessage = createMessageFromBroker(pushBasedConsumerTopic, messageBatch);
                LOGGER.info("[THREAD ID : " + Thread.currentThread().getId() + "] [SENDING] [Sending next message batch containing total " + messageBatch.size() + " messages to " + name + "]");
                System.out.printf("\n[THREAD ID : %s] [SENDING] [Sending next message batch containing total %d messages to %s.]\n", Thread.currentThread().getId(), messageBatch.size(), name);
                boolean sendSuccessful = connection.send(finalMessage);
                 if (sendSuccessful) {
                     for (byte[] eachMessage : messageBatch) {
                         offset += eachMessage.length; // updating offset value
                     }
                     LOGGER.debug("[THREAD ID : " + Thread.currentThread().getId() + "] Updated offset : " + offset);
                     System.out.printf("\n[THREAD ID : %s] Updated offset : %d\n", Thread.currentThread().getId(), offset);
                 }
            } else {
                try {
                    LOGGER.debug("[THREAD ID : " + Thread.currentThread().getId() + "] [SLEEPING for 500 millis] [Next Batch of message is not yet available on Broker]");
                    System.out.printf("\n[THREAD ID : %s] [SLEEPING for 500 millis] [Next Batch of message is not yet available on Broker]\n", Thread.currentThread().getId());
                    Thread.sleep(500); //sleeping for 500 millisecond
                } catch (InterruptedException e) {
                    LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InterruptedException : " + e);
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
    private byte[] createMessageFromBroker(String topic, ArrayList<byte[]> messageBatch) {
        ArrayList<ByteString> messageBatchStringArray = new ArrayList<>();
        for (byte[] eachMessage : messageBatch) {
            ByteString messageByteString = ByteString.copyFrom(eachMessage);
            messageBatchStringArray.add(messageByteString);
        }
        MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.newBuilder()
                .setType(Constants.MESSAGE)
                .setTopic(topic)
                .setTotalMessage(messageBatch.size())
                .addAllActualMessage(messageBatchStringArray)
                .build();
        return messageFromBrokerDetails.toByteArray();
    }

    /**
     * method creates MessageFromBroker obj of type INVALID
     * and returns its byteArray.
     * @return
     */
    private byte[] createMessageFromBrokerInvalid(String type) {
        MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.newBuilder()
                .setType(type)
                .build();
        return messageFromBrokerDetails.toByteArray();
    }
}
