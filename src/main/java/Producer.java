import com.google.protobuf.ByteString;
import model.Constants;
import proto.InitialMessage;
import proto.Packet;
import proto.PublisherPublishMessage;

import java.io.IOException;

/**
 *
 */
public class Producer extends Node {

    /**
     * constructor for producer class attributes
     * @param brokerIP
     * @param brokerPortNumber
     */
    public Producer (String producerName, String brokerIP, int brokerPortNumber) {
        super(producerName, brokerIP, brokerPortNumber);
    }

    /**
     *
     */
    public void startProducer() {
        boolean connected = connectToBroker();
        if (connected) {
            sendInitialSetupMessage();
        }
    }

    public void sendInitialSetupMessage() {
        //send initial message
        System.out.printf("\n[Creating Initial packet]\n");
        byte[] initialMessagePacket = createInitialMessagePacket();
        System.out.printf("\n[Sending Initial packet]\n");
        connection.send(initialMessagePacket); //sending initial packet
        System.out.printf("\n[Initial packet Sending Successful]\n");
    }

    /**
     *
     * @return
     */
    private byte[] createInitialMessagePacket() {
        InitialMessage.InitialMessageDetails initialMessageDetails = InitialMessage.InitialMessageDetails.newBuilder()
                .setConnectionSender(Constants.PRODUCER)
                .setName(name)
                .build();
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(brokerIP)
                .setFrom(name)
                .setType(Constants.INITIAL_SETUP)
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
        boolean sent = connection.send(publisherPublishMessageDetails.toByteArray());
        return sent;
    }

    /**
     *
     */
    public void close() {
        try {
            connection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
