import model.Constants;
import model.MessageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
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
    private static final Logger logger = LogManager.getLogger(Broker.class);
    private boolean shutdown = false;
    private String brokerName;
    private String brokerIP;
    private int brokerPort;
    private ExecutorService threadPool = Executors.newFixedThreadPool(Constants.BROKER_THREAD_POOL_SIZE);

    /**
     * Constructor
     * @param brokerName Name of this broker
     * @param brokerIP Ip of this broker
     * @param brokerPort Port on which this broker is running
     */
    public Broker(String brokerName, String brokerIP, int brokerPort) {
        this.brokerName = brokerName;
        this.brokerIP = brokerIP;
        this.brokerPort = brokerPort;
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
            serverSocket.bind(new InetSocketAddress(brokerIP, brokerPort));
            // keeps on running when shutdown is false
            while (!shutdown) {
                logger.info("\n[Broker : " + brokerName + " BrokerServer is listening on IP : " + brokerIP + " & Port : " + brokerPort);
                Future<AsynchronousSocketChannel> acceptFuture = serverSocket.accept();
                AsynchronousSocketChannel socketChannel = null;

                try {
                    socketChannel = acceptFuture.get();
                    if (shutdown) {
                        return;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("\nException while establishing connection. Error Message : " + e.getMessage());
                }

                //checking if the socketChannel is valid.
                if ((socketChannel != null) && (socketChannel.isOpen())) {
                    Connection newConnection = null;
                    newConnection = new Connection(socketChannel);
                    // give this connection to requestProcessor
                    RequestProcessor requestProcessor = new RequestProcessor(brokerName, newConnection);
                    threadPool.execute(requestProcessor);
                }
            }
        } catch (IOException e) {
            logger.error("\nIOException while opening serverSocket connection. Error Message : " + e.getMessage());
        }
    }
}
