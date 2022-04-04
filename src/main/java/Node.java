import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Parent class for Producer and Consumer class.
 * @author nilimajha
 */
public class Node {
    private static final Logger logger = LogManager.getLogger(Node.class);
    protected String name;
    protected String brokerIP;
    protected int brokerPort;
    protected Connection connection;
    protected boolean connected;

    /**
     * constructor for producer class attributes
     * @param brokerIP Ip of Broker
     * @param brokerPort Broker port
     */
    public Node (String name, String brokerIP, int brokerPort) {
        this.name = name;
        this.brokerIP = brokerIP;
        this.brokerPort = brokerPort;
    }

    /**
     * method that connects to the broker and saves the connection object.
     * @return  true/false
     */
    public boolean connectToBroker() {
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
            InetSocketAddress brokerAddress = new InetSocketAddress(brokerIP, brokerPort);
            logger.info("\n[Connecting To Broker]");
            Future<Void> futureSocket = clientSocket.connect(brokerAddress);
            try {
                futureSocket.get();
                logger.info("\n[Connection Successful]");
                connection = new Connection(clientSocket);
                connected = true;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("\nException occurred while connecting to broker. Error Message : " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("\nIOException occurred while connecting to Broker.");
        }
        return connected;
    }

    /**
     * getter for the name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public boolean connectedToBroker() {
        return connected;
    }

//    /**
//     * Wrap the message into a packet of given type.
//     * @param message
//     * @param type
//     * @return
//     */
//    public byte[] createPacket(ByteString message, String type) {
//        Packet.PacketDetails packetDetails = Packet.PacketDetails.newBuilder()
//                .setTo(brokerIP)
//                .setFrom(name)
//                .setType(type)
//                .setMessage(message)
//                .build();
//        return packetDetails.toByteArray();
//    }
}
