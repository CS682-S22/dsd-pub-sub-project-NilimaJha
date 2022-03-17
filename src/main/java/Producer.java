import com.google.protobuf.ByteString;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import proto.InitialMessage;
import proto.Packet;
import proto.PublisherPublishMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Producer class to produce message on the broker on any specific topic.
 * @author nilimajha
 */
public class Producer {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Producer.class);
    private String producerName;
    private String brokerIP;
    private int brokerPortNumber;
    private Connection connection;

    /**
     * constructor for producer class attributes
     * @param brokerIP
     * @param brokerPortNumber
     */
    public Producer (String producerName, String brokerIP, int brokerPortNumber) {
        this.producerName = producerName;
        this.brokerIP = brokerIP;
        this.brokerPortNumber = brokerPortNumber;
    }

    /**
     * method set up connection with broker.
     */
    public void startProducer() {
        connectToBroker();
    }

    /**
     * method creates a connection socket and connects with the broker running at the given ip and port.
     * and sends the initial setup packet to the broker.
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
     * creates the initial set message packet to be sent to the broker.
     * @return byte array of the initialMessage packet.
     */
    private byte[] createInitialMessagePacket() {
        InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                .setConnectionSender(Constants.PRODUCER)
                .setName(producerName)
                .build();
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(brokerIP)
                .setFrom(producerName)
                .setType(Constants.INITIAL)
                .setMessage(initialMessageDetails.toByteString())
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * send method takes the message to be published on the broker and
     * also the topic to which this message will be published on broker.
     * @param topic
     * @param data
     * @return
     */
    public boolean send (String topic, byte[] data) {
        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.newBuilder()
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build();
        LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [SENDING] Sending message of topic " + topic + "to the broker.");
        System.out.printf("\n[Thread Id : %s] [SENDING] Sending message of topic %s to the broker.\n", Thread.currentThread().getId(), topic);
        boolean sent = connection.send(publisherPublishMessageDetails.toByteArray());
        return sent;
    }

    /**
     * closes the producer by closing the connection socket.
     */
    public void close() {
        try {
            connection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
