import com.google.protobuf.ByteString;
import model.Constants;
import proto.InitialMessage;
import proto.Packet;
import proto.PublisherPublishMessage;

import java.io.IOException;

/**
 * Class extends Node class and is a producer
 * @author nilimajha
 */
public class Producer extends Node {

    /**
     * constructor for producer class attributes
     * @param brokerIP Ip of Broker
     * @param brokerPort port on which broker is running
     */
    public Producer (String producerName, String brokerIP, int brokerPort) {
        super(producerName, brokerIP, brokerPort);
    }

    /**
     * first connects to the broker and then sets itself up by sending InitialPacket.
     */
    public void startProducer() {
        boolean connected = connectToBroker();
        if (connected) {
            sendInitialSetupMessage();
        }
    }

    /**
     * creates and sends Initial Packet to the Broker.
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
     * creates the Producer Initial packet.
     * @return byte[] array
     */
    public byte[] createInitialMessagePacket() {
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
     * @param topic topic of the data
     * @param data actual data to be published
     * @return true/false
     */
    public boolean send (String topic, byte[] data) {
        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.newBuilder()
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build();
        return connection.send(publisherPublishMessageDetails.toByteArray());
    }

    /**
     * closes the connection.
     */
    public void close() {
        try {
            connection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
