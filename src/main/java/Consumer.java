import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
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
 * Consumer class to consume message from the broker on any specific topic.
 * This class can be of two type either pull or push.
 * @author nilimajha
 */
public class Consumer {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Consumer.class);
    private String consumerName;
    private String consumerType;
    private String brokerIP;
    private int brokerPortNumber;
    private Connection connection;
    private int offset;
    private String topic;
    private BlockingQueue<byte[]> messageFromBroker;
//    ExecutorService pool = Executors.newFixedThreadPool(1); //thread pool of size 1

    /**
     * Constructor to initialise the attribute of this class.
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
//        pool.execute(this :: startConsumer);
    }

    /**
     * receive data from broker by calling appropriate function as per the time of consumer
     */
    public void startConsumer() {
        connectToBroker();
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
//                System.out.printf("\n[Receiving message from broker]\n");
                receiveMessageFromBroker(); // receiving data from broker
            }
        }

    }

    /**
     * method establishes connection with broker and sends initial message to it.
     */
    public boolean connectToBroker() {
        boolean connected = false;
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught IOException : " + e);
        }
        InetSocketAddress brokerAddress = new InetSocketAddress(brokerIP, brokerPortNumber);
        Future<Void> futureSocket = clientSocket.connect(brokerAddress);
        try {
            futureSocket.get();
            LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] Connected to Broker.");
            System.out.printf("\n[Thread Id : %s] Connected to Broker.\n", Thread.currentThread().getId());
            connection = new Connection(clientSocket);
            //send initial message
            LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] Creating Initial packet.");
            byte[] initialMessagePacket = createInitialMessagePacket();
            connection.send(initialMessagePacket); //sending initial packet
            LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [SENT] Sent Initial packet.");
            System.out.printf("\n[Thread Id : %s] [SENT] Sent Initial packet\n", Thread.currentThread().getId());
            connected = true;
        } catch (InterruptedException e) {
            LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InterruptedException : " + e);
        } catch (ExecutionException e) {
            LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught ExecutionException : " + e);
        }
        return connected;
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
        LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [SENT] Sent Pull Request to Broker for offset " + offset);
        System.out.printf("\n[Thread Id : %s] [SENT] Sent Pull Request to Broker for offset %d\n", Thread.currentThread().getId(), offset);
        connection.send(requestMessage); // sending pull request to the broker
        return receiveMessageFromBroker();
    }

    /**
     * method receive message from broker.
     */
    private boolean receiveMessageFromBroker() {
        boolean successful = true;
        byte[] brokerMessage = connection.receive();
        if (brokerMessage != null) {
            successful = extractMessageFromBrokerMessage(brokerMessage);
        }
        return successful;
    }

    /**
     * extract actual message from message provided by the Broker.
     * @param brokerMessage
     */
    private boolean extractMessageFromBrokerMessage(byte[] brokerMessage) {
        boolean success = false;
        if (brokerMessage != null) {
            MessageFromBroker.MessageFromBrokerDetails messageFromBrokerDetails;
            try {
                messageFromBrokerDetails = MessageFromBroker.MessageFromBrokerDetails.parseFrom(brokerMessage);
                if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE)) {
                    LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [RECEIVED] Received response from Broker. Total message in one response : " + messageFromBrokerDetails.getActualMessageCount());
                    System.out.printf("\n[Thread Id : %s] [RECEIVED] Received message from Broker. Total message in one response : %d\n", Thread.currentThread().getId(), messageFromBrokerDetails.getActualMessageCount());
                    for (int index = 0; index < messageFromBrokerDetails.getActualMessageCount(); index++) {
                        byte[] actualMessageBytes = messageFromBrokerDetails.getActualMessage(index).toByteArray();
                        messageFromBroker.put(actualMessageBytes);
                        if (consumerType.equals(model.Constants.CONSUMER_PULL)) {
                            offset += actualMessageBytes.length; // incrementing offset value to the next message offset
                        }
                    }
                    success = true;
                } else if (messageFromBrokerDetails.getType().equals(Constants.MESSAGE_NOT_AVAILABLE)) {
                    LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [RECEIVED] Received response from Broker.  Message of offset " + offset + " is not available yet on the Broker, so sleeping for 6000 millis.");
                    System.out.printf("\n[Thread Id : %s] [RECEIVED] Received message from Broker. Message of offset %d is not available yet on the Broker, so sleeping for 6000 millis.\n", Thread.currentThread().getId(), offset);
                    Thread.sleep(6000);
                    success = true;
                }
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("[Thread Id : "+ Thread.currentThread().getId() + "] Caught InvalidProtocolBufferException " + e);
            } catch (InterruptedException e) {
                LOGGER.error("[Thread Id : "+ Thread.currentThread().getId() + "] Caught InterruptedException " + e);
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
            LOGGER.error("[Thread Id : "+ Thread.currentThread().getId() + "] Caught InterruptedException " + e);
        }
        return message;
    }
}
