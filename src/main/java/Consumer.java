import com.google.protobuf.InvalidProtocolBufferException;
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
    }

    /**
     * receive data from broker by calling appropriate function as per the time of consumer
     */
    public void startConsumer() {
        connectToBroker();
        if (this.consumerType == Constants.CONSUMER_TYPE_PULL) {
            while (this.newConnection.connectionSocket.isOpen()) {
                pullMessageFromBroker(); // fetching data from broker
            }
        } else {
            while (this.newConnection.connectionSocket.isOpen()) {
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
        InetSocketAddress peerAddress = new InetSocketAddress(this.brokerIP, this.brokerPortNumber);
        Future<Void> futureSocket = clientSocket.connect(peerAddress);
        try {
            futureSocket.get();
            this.newConnection = new Connection(this.brokerIP, clientSocket);
            //send initial message;
            byte[] initialMessagePacket = createInitialMessagePacket();
            this.newConnection.send(initialMessagePacket); //sending initial packet
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
        if (this.consumerType.equals(Constants.CONSUMER_PULL)) {
            initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionFrom(Constants.CONSUMER)
                    .setConsumerType(Constants.CONSUMER_PULL)
                    .build();
        } else {
            initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                    .setConnectionFrom(Constants.CONSUMER)
                    .setConsumerType(Constants.CONSUMER_PUSH)
                    .setTopic(this.topic)
                    .setInitialOffset(this.offset)
                    .build();
        }
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(this.brokerIP)
                .setFrom(this.consumerName)
                .setType(Constants.INITIAL)
                .setMessage(initialMessageDetails.toByteString())
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * method creates pull request message.
     * @return byte[]
     */
    public byte[] createPullRequestMessage() {
        ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.newBuilder()
                .setTopic(this.topic)
                .setOffset(this.offset)
                .build();
        return consumerPullRequestDetails.toByteArray();
    }

    /**
     * method pulls message from broker a
     * t first it sends pull message to the broker
     * and then receives message sent by broker.
     */
    public void pullMessageFromBroker() {
        byte[] requestMessage = createPullRequestMessage();
        this.newConnection.send(requestMessage);
        byte[] responseFromBroker = this.newConnection.receive();
        extractMessageFromBrokerMessage(responseFromBroker);
    }

    /**
     * method receive message from broker.
     */
    public void receiveMessageFromBroker() {
        byte[] brokerMessage = this.newConnection.receive();
        extractMessageFromBrokerMessage(brokerMessage);
    }

    /**
     *
     * @param brokerMessage
     */
    private void extractMessageFromBrokerMessage(byte[] brokerMessage) {
        if (brokerMessage != null) {
            MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails;
            try {
                messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.parseFrom(brokerMessage);
                if (messageFromBrokerDetails.getType() == "MESSAGE") {
                    for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                        byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                        this.messageFromBroker.put(actualMessageBytes);
                        if (this.consumerType == Constants.CONSUMER_TYPE_PULL) {
                            this.offset += actualMessageBytes.length; // incrementing offset value to the next message offset
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
