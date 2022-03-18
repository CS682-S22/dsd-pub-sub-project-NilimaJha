import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import proto.ConsumerPullRequest;
import proto.InitialMessage;
import proto.MessageFromBroker;
import proto.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.Duration;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 *
 * @author nilimajha
 */
public class Consumer {
    private String consumerName;
    private String consumerType;
    private String brokerIP;
    private int brokerPortNumber;
    private Connection newConnection;
    private int offset;
    private String topic;
    private BlockingQueue<byte[]> messageFromBroker;
    private ExecutorService pool = Executors.newFixedThreadPool(1); //thread pool of size 1

    /**
     *
     * @param consumerName
     * @param consumerType
     * @param brokerIP
     * @param brokerPortNumber
     * @param topic
     * @param startingPosition
     */
    public Consumer(String consumerName, String consumerType, String brokerIP, int brokerPortNumber, String topic, int startingPosition) {
        this.consumerName = consumerName;
        this.consumerType = consumerType;
        this.brokerIP = brokerIP;
        this.brokerPortNumber = brokerPortNumber;
        this.topic = topic;
        this.offset = startingPosition;
        this.messageFromBroker = new LinkedBlockingQueue<>();
        pool.execute(this::startConsumer);  // assigning consumer thread
    }

    /**
     * receive data from broker by calling appropriate function as per the time of consumer
     */
    public void startConsumer() {
        connectToBroker();
        if (consumerType.equals(Constants.CONSUMER_PULL)) {
            while (newConnection.connectionSocket.isOpen()) {
                System.out.printf("\n[Pulling message from broker]\n");
                boolean topicIsAvailable = pullMessageFromBroker(); // fetching data from broker
                if (!topicIsAvailable) {
                    break;
                }
            }
        } else {
            while (newConnection.connectionSocket.isOpen()) {
//                System.out.printf("\n[Receiving message from broker]\n");
                receiveMessageFromBroker(); // receiving data from broker
            }
        }

    }

    /**
     * method establishes connection with broker and sends initial message to it.
     */
    public void connectToBroker() {
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetSocketAddress peerAddress = new InetSocketAddress(brokerIP, brokerPortNumber);
        System.out.printf("\n[Connecting To Broker]\n");
        Future<Void> futureSocket = clientSocket.connect(peerAddress);
        try {
            futureSocket.get();
            System.out.printf("\n[Connection Successful]\n");
            newConnection = new Connection(brokerIP, clientSocket);
            //send initial message
            System.out.printf("\n[Creating Initial packet]\n");
            byte[] initialMessagePacket = createInitialMessagePacket();
            System.out.printf("\n[Sending Initial packet]\n");
            newConnection.send(initialMessagePacket); //sending initial packet
            System.out.printf("\n[Initial packet Sending Successful]\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
                    .setName(consumerName)
                    .setConsumerType(Constants.CONSUMER_PULL)
                    .build();
        } else {
            initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionSender(Constants.CONSUMER)
                    .setName(consumerName)
                    .setConsumerType(Constants.CONSUMER_PUSH)
                    .setTopic(topic)
                    .setInitialOffset(offset)
                    .build();
        }
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(brokerIP)
                .setFrom(consumerName)
                .setType(Constants.INITIAL)
                .setMessage(initialMessageDetails.toByteString())
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * method creates pull request message.
     * @return byte[]
     */
    private byte[] createPullRequestMessage() {
        System.out.printf("\nSending pull request for offset number %d\n", offset);
        ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.newBuilder()
                .setTopic(topic)
                .setOffset(offset)
                .build();
        return consumerPullRequestDetails.toByteArray();
    }

    /**
     * method pulls message from broker a
     * t first it sends pull message to the broker
     * and then receives message sent by broker.
     */
    private boolean pullMessageFromBroker() {
        byte[] requestMessage = createPullRequestMessage();
        System.out.printf("\n[SEND]Send pull request to Broker\n");
        newConnection.send(requestMessage); // sending pull request to the broker
        return receiveMessageFromBroker();
    }

    /**
     * method receive message from broker.
     */
    private boolean receiveMessageFromBroker() {
        boolean successful = true;
        byte[] brokerMessage = newConnection.receive();
        if (brokerMessage != null) {
            System.out.printf("\n[RECEIVE]Received message from Broker\n");
            successful = extractMessageFromBrokerMessage(brokerMessage);
        }
        return successful;
    }

    /**
     *
     * @param brokerMessage
     */
    private boolean extractMessageFromBrokerMessage(byte[] brokerMessage) {
        boolean success = false;
        if (brokerMessage != null) {
            MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails;
            try {
                messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.parseFrom(brokerMessage);
                if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE)) {
                    System.out.printf("\n[RECEIVE] Total message received from broker in one response = %d.\n", messageFromBrokerDetails.getActualMessageCount());
                    for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                        byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                        messageFromBroker.put(actualMessageBytes);
                        System.out.printf("\nReceived Message size: %d\n", actualMessageBytes.length);
                        if (consumerType.equals(model.Constants.CONSUMER_PULL)) {
                            offset += actualMessageBytes.length; // incrementing offset value to the next message offset
                        }
                    }
                    success = true;
                } else if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE_NOT_AVAILABLE)) {
                    System.out.printf("\noffset number is not available yet, so sleeping for 6000 ms %d\n", offset);
                    Thread.sleep(6000);
                    success = true;
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * return application program the byte array of message fetched from broker.
     * @return message
     * @param duration
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
