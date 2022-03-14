import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import model.Data;
import model.DataInitializer;
import proto.*;

import java.util.ArrayList;

/**
 * class to handle connection between broker and producer or consumer.
 * @author nilimajha
 */
public class RequestProcessor implements Runnable {
    private Connection connection;
    private Data data;
    private String from;
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
        while (this.from == null) {
            byte[] receivedMessage = this.connection.receive(); // getting initial setup Message.
            if (receivedMessage != null) { // received initial message
                // call decode packet and then call decode message inside
                Packet.PacketDetails packetDetails = null;
                try {
                    packetDetails = Packet.PacketDetails.parseFrom(receivedMessage);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                parseInitialMessage(packetDetails);
            }
        }

        if (this.from == Constants.PRODUCER) {
            handlePublisher();
        } else if (this.from == Constants.CONSUMER_PULL) {
            handlePullConsumer();
        } else if (this.from == Constants.CONSUMER_PUSH) {
            handlePushConsumer();
        }
    }

    /**
     * decode message field of the PacketDetails object as per the type.
     * @param packetDetails
     */
    public boolean parseInitialMessage(Packet.PacketDetails packetDetails) {
        if (packetDetails.getType().equals(Constants.INITIAL)) {
            // decode message part from packetDetail to InitialMessage proto
            try {
                InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage());
                if (this.from == null) {
                    if (initialMessageDetails.getConnectionFrom() == Constants.PRODUCER) {
                        this.from = Constants.PRODUCER;
                    } else if (initialMessageDetails.getConnectionFrom() == Constants.CONSUMER && initialMessageDetails.getConsumerType() == Constants.CONSUMER_TYPE_PULL) {
                        this.from = Constants.CONSUMER_PULL; // type of consumer
                    } else if (initialMessageDetails.getConnectionFrom() == Constants.CONSUMER && initialMessageDetails.getConsumerType() == Constants.CONSUMER_TYPE_PUSH) {
                        this.from = Constants.CONSUMER_PUSH; // type of consumer
                        this.offset = initialMessageDetails.getInitialOffset();
                        this.pushBasedConsumerTopic = initialMessageDetails.getTopic();
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * receives publish message from publisher and add it to the topic.
     */
    public void handlePublisher() {
        while (this.connection.connectionSocket.isOpen()) {
            byte[] message = this.connection.receive();
            if (message != null) {
                try {
                    PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.parseFrom(message);
                    if (publisherPublishMessageDetails.getTopic() != null && publisherPublishMessageDetails.getMessage() != null) { // validating publish message
                        this.data.addMessage(publisherPublishMessageDetails.getTopic(), publisherPublishMessageDetails.getMessage().toByteArray());
                    }
                    // if publish message is not valid then doing nothing.
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePullConsumer() {
        while (this.connection.connectionSocket.isOpen()) {
            byte[] message = this.connection.receive();
            if (message != null) {
                try {
                    ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.parseFrom(message);
                    ArrayList<byte[]> messageBatch = null;
                    byte[] finalByteArray;
                    if (consumerPullRequestDetails.getTopic() != null) { // validating publish message
                        messageBatch = this.data.getMessage(consumerPullRequestDetails.getTopic(), consumerPullRequestDetails.getOffset());
                        if (messageBatch != null) {
                            finalByteArray = createMessageFromBroker(consumerPullRequestDetails.getTopic(), messageBatch);
                        } else {
                            // message with given offset is not available
                            finalByteArray = createMessageFromBrokerInvalid("MESSAGE_NOT_AVAILABLE");
                        }
                        // create MessageFromBroker obj and send it to the consumer

                    } else {
                        finalByteArray = createMessageFromBrokerInvalid("TOPIC_NOT_AVAILABLE");
                    }
                   this.connection.send(finalByteArray);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * method continuously listens for message from pull type consumer for
     * pull request and sends response accordingly.
     */
    public void handlePushConsumer() {
        while (this.connection.connectionSocket.isOpen()) {
            ArrayList<byte[]> messageBatch = null;
            messageBatch = this.data.getMessage(this.pushBasedConsumerTopic, this.offset);
            if (messageBatch != null) {
                byte[] finalMessage = createMessageFromBroker(this.pushBasedConsumerTopic, messageBatch);
                this.connection.send(finalMessage);
            } else {
                try {
                    Thread.sleep(500); //sleeping for 500 millisecond
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                .setType("MESSAGE")
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
