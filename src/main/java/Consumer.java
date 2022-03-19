import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import proto.ConsumerPullRequest;
import proto.InitialMessage;
import proto.MessageFromBroker;
import proto.Packet;

import java.time.Duration;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Class extends Node class and is a Consumer
 * @author nilimajha
 */
public class Consumer extends Node {
    private String consumerType;
    private int offset;
    private String topic;
    private BlockingQueue<byte[]> messageFromBroker;
    private ExecutorService pool = Executors.newFixedThreadPool(1); //thread pool of size 1

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
    public Consumer(String consumerName, String consumerType, String brokerIP, int brokerPort, String topic, int startingPosition) {
        super(consumerName, brokerIP, brokerPort);
        this.consumerType = consumerType;
        this.topic = topic;
        this.offset = startingPosition;
        this.messageFromBroker = new LinkedBlockingQueue<>();
        this.pool.execute(this::startConsumer);  // assigning consumer thread
    }

    /**
     * first connects to the broker and
     * sets itself up with Broker by sending InitialPacket.
     * then after receive data from broker by
     * calling appropriate function as per the time of consumer
     */
    public void startConsumer() {
        boolean connected = connectToBroker();
        if (connected) {
            sendInitialSetupMessage();
            if (consumerType.equals(Constants.CONSUMER_PULL)) {
                while (connection.connectionSocket.isOpen()) {
                    System.out.printf("\n[Pulling message from broker]\n");
                    boolean topicIsAvailable = pullMessageFromBroker(); // fetching data from broker
                    if (!topicIsAvailable) {
                        break;
                    }
                }
            } else {
                while (connection.connectionSocket.isOpen()) {
                    receiveMessageFromBroker(); // receiving data from broker
                }
            }
        }
    }

    /**
     * method sends Consumer Initial setup packet to the broker.
     */
    public void sendInitialSetupMessage() {
        //send initial message
        System.out.printf("\n[Creating Initial packet]\n");
        byte[] initialMessagePacket = createInitialMessagePacket();
        System.out.printf("\n[Sending Initial packet]\n");
        connection.send(initialMessagePacket); //sending initial packet
        System.out.printf("\n[Initial packet Sending Successful]\n");
    }

    /**
     * method creates appropriate Initial message for the broker as per the consumer type.
     * @return initialMessagePacketByteArray
     */
    private byte[] createInitialMessagePacket() {
        InitialMessage.InitialMessageDetails initialMessageDetails;
        if (consumerType.equals(Constants.CONSUMER_PULL)) {
            initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionSender(Constants.CONSUMER)
                    .setName(name)
                    .setConsumerType(Constants.CONSUMER_PULL)
                    .build();
        } else {
            initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionSender(Constants.CONSUMER)
                    .setName(name)
                    .setConsumerType(Constants.CONSUMER_PUSH)
                    .setTopic(topic)
                    .setInitialOffset(offset)
                    .build();
        }
        return createPacket(initialMessageDetails.toByteString(), Constants.INITIAL_SETUP);
    }

    /**
     * method creates pull request message.
     * @return byte[]
     */
    private byte[] createPullRequestMessagePacket() {
        System.out.printf("\nSending pull request for offset number %d\n", offset);
        ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.newBuilder()
                .setTopic(topic)
                .setOffset(offset)
                .build();
        return createPacket(consumerPullRequestDetails.toByteString(), Constants.PULL_REQUEST);
    }

    /**
     * method creates packet for message to be sent to the broker as per the consumer type.
     * @param byteMassage message to be sent
     * @param type type of message
     * @return byte[] packet to be sent
     */
    private byte[] createPacket(ByteString byteMassage, String type) {
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(brokerIP)
                .setFrom(name)
                .setType(type)
                .setMessage(byteMassage)
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * method pulls message from broker
     * at first it sends pull message to the broker
     * and then receives message sent by broker.
     */
    private boolean pullMessageFromBroker() {
        byte[] requestMessagePacket = createPullRequestMessagePacket();
        System.out.printf("\n[SEND]Send pull request to Broker\n");
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
            System.out.printf("\n[RECEIVE]Received message from Broker\n");
            successful = extractDataFromBrokerMessage(brokerMessage);
        }
        return successful;
    }

    /**
     * method extracts data from message received from broker.
     * @param brokerMessage message received from broker
     * @return true/false
     */
    private boolean extractDataFromBrokerMessage(byte[] brokerMessage) {
        boolean success = false;
        if (brokerMessage != null) {
            Packet.PacketDetails packetDetails;
            try {
                packetDetails = Packet.PacketDetails.parseFrom(brokerMessage);
                if (packetDetails.getType().equals(Constants.DATA)) {
                    MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.parseFrom(packetDetails.getMessage().toByteArray());
                    if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE)) {
                        System.out.printf("\n[RECEIVE] Total message received from broker in one response = %d.\n", messageFromBrokerDetails.getActualMessageCount());
                        for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                            byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                            try {
                                messageFromBroker.put(actualMessageBytes);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.printf("\nReceived Message size: %d\n", actualMessageBytes.length);
                            if (consumerType.equals(model.Constants.CONSUMER_PULL)) {
                                offset += actualMessageBytes.length; // incrementing offset value to the next message offset
                            }
                        }
                        success = true;
                    }
                } else if (packetDetails.getType().equals(Constants.MESSAGE_NOT_AVAILABLE) || packetDetails.getType().equals(Constants.TOPIC_NOT_AVAILABLE)) {
                    System.out.printf("\noffset number is not available yet, so sleeping for 6000 ms %d\n", offset);
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    success = true;
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
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
            e.printStackTrace();
        }
        return message;
    }
}
