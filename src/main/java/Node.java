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
    protected String name;
    protected String brokerIP;
    protected int brokerPort;
    protected Connection connection;

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
        boolean connected = false;
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetSocketAddress brokerAddress = new InetSocketAddress(brokerIP, brokerPort);
        System.out.printf("\n[Connecting To Broker]\n");
        Future<Void> futureSocket = clientSocket.connect(brokerAddress);
        try {
            futureSocket.get();
            System.out.printf("\n[Connection Successful]\n");
            connection = new Connection(clientSocket);
            connected = true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return connected;
    }
}
