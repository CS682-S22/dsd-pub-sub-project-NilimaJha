import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.InitialMessage;
import proto.PublisherPublishMessage;


/**
 * Class extends Node class and is a producer
 * @author nilimajha
 */
public class Producer extends Node {
    private static final Logger logger = LogManager.getLogger(Producer.class);

    /**
     * constructor for producer class attributes
     * @param brokerIP Ip of Broker
     * @param brokerPort port on which broker is running
     */
    public Producer (String producerName, String brokerIP, int brokerPort) {
        super(producerName, brokerIP, brokerPort);
        startProducer();
    }

    /**
     * first connects to the broker and then sets itself up by sending InitialPacket.
     */
    public void startProducer() {
        connectToBroker();
        if (connected) {
            sendInitialSetupMessage();
        }
    }

    /**
     * creates and sends Initial Packet to the Broker.
     */
    public boolean sendInitialSetupMessage() {
        //send initial message
        byte[] initialMessagePacket = createInitialMessagePacket1();
        logger.info("\n[Sending Initial packet]");
        return connection.send(initialMessagePacket); //sending initial packet
    }

    /**
     * creates the Producer Initial packet.
     * @return byte[] array
     */
    public byte[] createInitialMessagePacket1() {
        Any any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                .setConnectionSender(Constants.PRODUCER)
                .setName(name)
                .build());
        return any.toByteArray();
    }

    /**
     * method creates the publishMessage packet with the given message and topic.
     * @param topic to which the data belongs
     * @param data actual data to be published.
     * @return
     */
    public byte[] createPublishMessagePacket(String topic, byte[] data) {
        Any any = Any.pack(PublisherPublishMessage
                .PublisherPublishMessageDetails.newBuilder()
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build());
        PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails = PublisherPublishMessage
                .PublisherPublishMessageDetails.newBuilder()
                .setTopic(topic)
                .setMessage(ByteString.copyFrom(data))
                .build();
        //return createPacket(publisherPublishMessageDetails.toByteString(), Constants.PUBLISH_REQUEST);
        return any.toByteArray();
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
        logger.info("\n[SEND] Publishing Message on Topic " + topic);
        return connection.send(createPublishMessagePacket(topic, data));
    }

    /**
     * closes the connection.
     */
    public void close() {
        boolean closeSuccessful = false;
        while (!closeSuccessful) {
            closeSuccessful = connection.closeConnection();
        }
    }


}
