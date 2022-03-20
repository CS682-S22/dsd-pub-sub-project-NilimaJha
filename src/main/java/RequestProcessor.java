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
    private String brokerName;
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
    public RequestProcessor(String brokerName, Connection connection) {
        this.brokerName = brokerName;
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
                    e.printStackTrace();
                }
                if (packetDetails.getType().equals(Constants.INITIAL_SETUP)) {
                    System.out.printf("\n[Received InitialPacket]\n");
                    parseInitialMessage(packetDetails);
                }
            }
        }

        if (connectionWith.equals(Constants.PRODUCER)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was PRODUCER]\n", Thread.currentThread().getId());
            handlePublisher();
            System.out.printf("\n[Thread Id : %s] [Called Handling PRODUCER]\n", Thread.currentThread().getId());
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PULL)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was from CONSUMER named %s of type PULL]\n", name, Thread.currentThread().getId());
            handlePullConsumer();
            System.out.printf("\n[Thread Id : %s] [Called Handling PULL CONSUMER]\n", Thread.currentThread().getId());
        } else if (connectionWith.equals(Constants.CONSUMER) && consumerType.equals(Constants.CONSUMER_PUSH)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was CONSUMER of type PUSH]\n", Thread.currentThread().getId());
            handlePushConsumer();
            System.out.printf("\n[Thread Id : %s] [Called Handling PUSH CONSUMER]\n", Thread.currentThread().getId());
        }
    }

    /**
     * decode message field of the PacketDetails object as per the type.
     * @param packetDetails initialPacket
     */
    public boolean parseInitialMessage(Packet.PacketDetails packetDetails) {
        System.out.printf("\n[Parsing Initial Message]\n");
        if (packetDetails.getType().equals(Constants.INITIAL_SETUP)) {
            System.out.printf("\n[Packet received is of type Initial.]\n");
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
                    System.out.printf("\n[Thread Id : %s] [@@@@@ Final name =  %s]\n", Thread.currentThread().getId(), name);
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * continuously receives publish message from publisher and add it to the topic.
     */
    public void handlePublisher() {
        while (connection.connectionSocket.isOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                Packet.PacketDetails packetDetails;
                try {
                    packetDetails = Packet.PacketDetails.parseFrom(message);
                    if (packetDetails.getType().equals(Constants.PUBLISH_REQUEST)) {
                        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
                        if (publisherPublishMessageDetails.getTopic() != null && publisherPublishMessageDetails.getMessage() != null) { // validating publish message
                            data.addMessage(publisherPublishMessageDetails.getTopic(), publisherPublishMessageDetails.getMessage().toByteArray());
                        }
                        // if publish message is not valid then doing nothing.
                    }
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
        while (connection.connectionSocket.isOpen()) {
            byte[] message = connection.receive();
            if (message != null) {
                Packet.PacketDetails packetDetails;
                try {
                    packetDetails = Packet.PacketDetails.parseFrom(message);
                    if (packetDetails.getType().equals(Constants.PULL_REQUEST)) {
                        ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.parseFrom(packetDetails.getMessage());
                        ArrayList<byte[]> messageBatch = null;
                        byte[] messageFromBrokerPacket;
                        if (consumerPullRequestDetails.getTopic() != null) { // validating publish message
                            System.out.printf("\n[Thread Id : %s] Getting message from offset '%d' from file.\n", Thread.currentThread().getId(), consumerPullRequestDetails.getOffset());
                            messageBatch = data.getMessage(consumerPullRequestDetails.getTopic(), consumerPullRequestDetails.getOffset());
                            if (messageBatch != null) {
                                messageFromBrokerPacket = createMessageFromBroker(consumerPullRequestDetails.getTopic(), messageBatch, Constants.DATA);
                            } else {
                                // message with given offset is not available
                                messageFromBrokerPacket = createMessageFromBrokerInvalid(Constants.MESSAGE_NOT_AVAILABLE);
                            }
                        } else {
                            messageFromBrokerPacket = createMessageFromBrokerInvalid(Constants.TOPIC_NOT_AVAILABLE);
                        }
                        // sending response to the consumer
                        System.out.printf("\n[THREAD ID : %s] [SENDING] [Sending prepared message batch to %s.]\n", Thread.currentThread().getId(), name);
                        connection.send(messageFromBrokerPacket);
                    }
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
        while (connection.connectionSocket.isOpen()) {
            ArrayList<byte[]> messageBatch = null;
            System.out.printf("\n[Thread Id : %s] Getting message from offset '%d' from file.\n", Thread.currentThread().getId(), offset);
            messageBatch = data.getMessage(pushBasedConsumerTopic, offset);
            if (messageBatch != null) {
                byte[] messageFromBrokerPacket = createMessageFromBroker(pushBasedConsumerTopic, messageBatch, Constants.DATA);
                System.out.printf("\n[THREAD ID : %s] SENDING next Batch of message to PUSH consumer %s.\n", Thread.currentThread().getId(), name);
                boolean sendSuccessful = connection.send(messageFromBrokerPacket);
                 if (sendSuccessful) {

                     for (byte[] eachMessage : messageBatch) {
                         offset += eachMessage.length; // updating offset value
                         System.out.printf("\nCurrent Message length: %d\n", eachMessage.length);
                     }
                     System.out.printf("\nNext offset : %d\n", offset);
                 }
            } else {
                try {
                    System.out.printf("\n[THREAD ID : %s]Next Batch of message is not yet PUSHED by Broker for the consumer, %s.\n", Thread.currentThread().getId(), name);
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
    private byte[] createMessageFromBroker(String topic, ArrayList<byte[]> messageBatch, String type) {
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
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(connectionWith)
                .setFrom(brokerName)
                .setType(type)
                .setMessage(messageFromBrokerDetails.toByteString())
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * method creates MessageFromBroker obj of type INVALID
     * and returns its byteArray.
     * @return byte[]
     */
    public byte[] createMessageFromBrokerInvalid(String type) {
        MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.newBuilder()
                .setType(type)
                .build();
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(connectionWith)
                .setFrom(brokerName)
                .setType(type)
                .setMessage(messageFromBrokerDetails.toByteString())
                .build();
        return packetDetails.toByteArray();
    }
}
