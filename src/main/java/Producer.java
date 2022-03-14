import com.google.protobuf.ByteString;
import proto.InitialMessage;
import proto.Packet;
import proto.PublisherPublishMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 */
public class Producer {
    private String producerName;
    private String brokerIP;
    private int brokerPortNumber;
    private Connection newConnection;

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
     *
     */
    public void startProducer() {
        connectToBroker();
    }

    /**
     *
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
     *
     * @return
     */
    private byte[] createInitialMessagePacket() {
        InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                .setConnectionFrom(Constants.PRODUCER)
                .build();
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(this.brokerIP)
                .setFrom(this.producerName)
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
        boolean sent = this.newConnection.send(publisherPublishMessageDetails.toByteArray());
        return sent;
    }

    /**
     *
     */
    public void close() {
        try {
            this.newConnection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
