import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Broker class that keeps a serverSocket open to
 * listen for new connection request from producer and consumer.
 * Creates new RequestProcessor object for each incoming request.
 * @author nilimajha
 */
public class Broker implements Runnable {
    HashMap<String, ArrayList<byte[]>> topicToMessageMap;
    private boolean shutdown = false;
    private String hostName;
    private String hostIP;
    private int portNumber;
    private ExecutorService threadPool = Executors.newFixedThreadPool(15);

    /**
     * Constructor
     * @param hostName
     * @param portNumber
     */
    public Broker(String hostName, String hostIP, int portNumber) {
        this.hostName = hostName;
        this.hostIP = hostIP;
        this.portNumber = portNumber;
    }

    /**
     * run opens a serverSocket and keeps listening for
     * new connection request from producer or consumer.
     * once it receives a connection request it creates a
     * connection object and hands it to the RequestProcessor class object.
     */
    @Override
    public void run() {
        AsynchronousServerSocketChannel serverSocket = null;
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(this.hostIP, this.portNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // keeps on running when shutdown is false
        while (!this.shutdown) {
            System.out.printf("%s[BROKER] BrokerServer is listening on %s, port : %s \n", this.hostName ,this.hostIP , this.portNumber);
            Future<AsynchronousSocketChannel> acceptFuture = serverSocket.accept();
            AsynchronousSocketChannel socketChannel = null;
            try {
                socketChannel = acceptFuture.get();
                if (this.shutdown) {
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //checking if the socketChannel is valid.
            if ((socketChannel != null) && (socketChannel.isOpen())) {
                try {
                    System.out.printf("\n[CONNECTION REQUEST] Connection Established with %s \n\n",
                            socketChannel.getRemoteAddress().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //creating new connection object adding it to the map and assigning to the threadPool.????
                Connection newConnection;
                try {
                    newConnection = new Connection(socketChannel.getRemoteAddress().toString(), socketChannel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // give this connection to requestProcessor
                // request processor will receive message and
                // if it is post type then it will check for topic and will append it to the
                // if it is get type then it will decode message and will extract type and message id and then will send next
                // 10 message at a time
                // if it is of type subscribe then requestProcessor will
                // -check if the topic is available if      yes ->then subscribe successful and
                //                                          no  ->then invalid topic subscribe unsuccessful and closing connection
                //           - if the topic is valid then check if the message id is available? if yes send to consumer pool and will continuously check if the
            }
        }
    }
}
