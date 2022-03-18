import com.google.protobuf.ByteString;
import model.Constants;
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
     *
     * @return
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
        boolean sent = newConnection.send(publisherPublishMessageDetails.toByteArray());
        return sent;
    }

    /**
     *
     */
    public void close() {
        try {
            newConnection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
