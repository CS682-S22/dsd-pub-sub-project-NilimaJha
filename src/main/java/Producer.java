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
    public boolean sendInitialSetupMessage() {
        //send initial message
        //System.out.printf("\n[Thread Id : %s] [Creating Initial packet]\n", Thread.currentThread().getId());
        byte[] initialMessagePacket = createInitialMessagePacket();
        System.out.printf("\n[Thread Id : %s] [Sending Initial packet]\n", Thread.currentThread().getId());
        return connection.send(initialMessagePacket); //sending initial packet
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
        return createPacket(initialMessageDetails.toByteString(), Constants.INITIAL_SETUP);
    }

    /**
     * method creates the publishMessage packet with the given message and topic.
     * @param topic to which the data belongs
     * @param data actual data to be published.
     * @return
     */
    public byte[] createPublishMessagePacket(String topic, byte[] data) {
        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.newBuilder()
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build();
        return createPacket(publisherPublishMessageDetails.toByteString(), Constants.PUBLISH_REQUEST);
    }

    /**
     * Wrap the message into a packet of given type.
     * @param message
     * @param type
     * @return
     */
    public byte[] createPacket(ByteString message, String type) {
        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
                .setTo(brokerIP)
                .setFrom(name)
                .setType(type)
                .setMessage(message)
                .build();
        return packetDetails.toByteArray();
    }

    /**
     * it takes the message to be published on the broker and
     * also the topic to which this message will be published on broker and
     * sends over the connection established with the broker.
     * @param topic topic of the data
     * @param data actual data to be published
     * @return true/false
     */
    public boolean send (String topic, byte[] data) {
        System.out.printf("\n[Thread Id : %s] [SEND] Publishing Message on Topic %s.\n", Thread.currentThread().getId(), topic);
        return connection.send(createPublishMessagePacket(topic, data));
    }

    /**
     * closes the connection.
     */
    public void close() {
        try {
            System.out.printf("\n[Thread Id : %s] Closing the Consumer.\n", Thread.currentThread().getId());
            connection.connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
