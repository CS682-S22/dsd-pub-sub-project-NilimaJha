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
        while (this.connectionWith == null) {
            byte[] receivedMessage = this.connection.receive(); // getting initial setup Message.
            if (receivedMessage != null) { // received initial message
                // call decode packet and then call decode message inside
                Packet.PacketDetails packetDetails = null;
                try {
                    packetDetails = Packet.PacketDetails.parseFrom(receivedMessage);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                System.out.printf("\n[Received InitialPacket]\n");
                parseInitialMessage(packetDetails);
            }
        }

        if (this.connectionWith.equals(Constants.PRODUCER)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was PRODUCER]\n", Thread.currentThread().getId());
            handlePublisher();
            System.out.printf("\n[Thread Id : %s] [Called Handling PRODUCER]\n", Thread.currentThread().getId());
        } else if (this.connectionWith.equals(Constants.CONSUMER) && this.consumerType.equals(Constants.CONSUMER_PULL)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was from CONSUMER named %s of type PULL]\n", this.name, Thread.currentThread().getId());
            handlePullConsumer();
            System.out.printf("\n[Thread Id : %s] [Called Handling PULL CONSUMER]\n", Thread.currentThread().getId());
        } else if (this.connectionWith.equals(Constants.CONSUMER) && this.consumerType.equals(Constants.CONSUMER_PUSH)) {
            System.out.printf("\n[Thread Id : %s] [This Connection was CONSUMER of type PUSH]\n", Thread.currentThread().getId());
            handlePushConsumer();
            System.out.printf("\n[Thread Id : %s] [Called Handling PUSH CONSUMER]\n", Thread.currentThread().getId());
        }
    }

    /**
     * decode message field of the PacketDetails object as per the type.
     * @param packetDetails
     */
    public boolean parseInitialMessage(Packet.PacketDetails packetDetails) {
        System.out.printf("\n[Parsing Initial Message]\n");
        if (packetDetails.getType().equals(Constants.INITIAL)) {
            System.out.printf("\n[Packet received is of type Initial.]\n");
            // decode message part from packetDetail to InitialMessage proto
            try {
                InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage());
                if (this.connectionWith == null) {
                    if (initialMessageDetails.getConnectionSender().equals(Constants.PRODUCER)) {
                        this.connectionWith = Constants.PRODUCER;
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) && initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PULL)) {
                        this.connectionWith = Constants.CONSUMER;
                        this.consumerType = Constants.CONSUMER_PULL;    // type of consumer
                    } else if (initialMessageDetails.getConnectionSender().equals(Constants.CONSUMER) && initialMessageDetails.getConsumerType().equals(Constants.CONSUMER_PUSH)) {
                        this.connectionWith = Constants.CONSUMER;
                        this.consumerType = Constants.CONSUMER_PUSH;    // type of consumer
                        this.offset = initialMessageDetails.getInitialOffset();
                        this.pushBasedConsumerTopic = initialMessageDetails.getTopic();
                    }
                    this.name = initialMessageDetails.getName();
                    System.out.printf("\n[@@@@@ Final name =  %s]\n", this.name);
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
//                        System.out.printf("\n[Adding new message] [Topic : %s]\n", publisherPublishMessageDetails.getTopic());
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
//                        System.out.printf("\n[Topic was not null in the pull request.] [offset number : %d]\n", consumerPullRequestDetails.getOffset());
                        System.out.printf("\n[Thread Id : %s] Getting message from offset '%d' from file.\n", Thread.currentThread().getId(), consumerPullRequestDetails.getOffset());
                        messageBatch = this.data.getMessage(consumerPullRequestDetails.getTopic(), consumerPullRequestDetails.getOffset());
//                        System.out.printf("\n[Preparing message to be sent to %s]\n", consumerPullRequestDetails.getOffset(), this.name);
                        if (messageBatch != null) {
//                            System.out.printf("\n[Topic was not null in the pull request.] [offset number : %d]\n", consumerPullRequestDetails.getOffset());
                            finalByteArray = createMessageFromBroker(consumerPullRequestDetails.getTopic(), messageBatch);
                        } else {
                            // message with given offset is not available
                            finalByteArray = createMessageFromBrokerInvalid("MESSAGE_NOT_AVAILABLE");
                        }
                    } else {
                        finalByteArray = createMessageFromBrokerInvalid("TOPIC_NOT_AVAILABLE");
                    }
                    // sending response to the consumer
                    System.out.printf("\n[THREAD ID : %s] [SENDING] [Sending prepared message batch to %s.]\n", Thread.currentThread().getId(), this.name);
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
            System.out.printf("\n[Thread Id : %s] Getting message from offset '%d' from file.\n", Thread.currentThread().getId(), this.offset);
            messageBatch = this.data.getMessage(this.pushBasedConsumerTopic, this.offset);
            if (messageBatch != null) {
                byte[] finalMessage = createMessageFromBroker(this.pushBasedConsumerTopic, messageBatch);
                System.out.printf("\n[THREAD ID : %s] SENDING next Batch of message to PUSH consumer %s.\n", Thread.currentThread().getId(), this.name);
                boolean sendSuccessful = this.connection.send(finalMessage);
                 if (sendSuccessful) {

                     for (byte[] eachMessage : messageBatch) {
                         this.offset += eachMessage.length; // updating offset value
                         System.out.printf("\nCurrent Message length: %d\n", eachMessage.length);
                     }
                     System.out.printf("\nNext offset : %d\n", this.offset);
                 }
            } else {
                try {
                    System.out.printf("\n[THREAD ID : %s]Next Batch of message is not yet PUSHED by Broker for the consumer, %s.\n", Thread.currentThread().getId(), this.name);
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
