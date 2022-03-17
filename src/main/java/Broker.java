import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

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
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Broker.class);
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
            serverSocket.bind(new InetSocketAddress(hostIP, portNumber));
        } catch (IOException e) {
            LOGGER.error("Caught IOException : " + e.getMessage());
        }

        // keeps on running when shutdown is false
        while (!shutdown) {
            LOGGER.info("[Thread Id : "+ Thread.currentThread().getId() + "] [" + hostName + "] BrokerServer is listening on " + hostIP + ", port : " + portNumber);
            System.out.printf("[Thread Id : %s] [%s] BrokerServer is listening on %s, port : %s \n", Thread.currentThread().getId(), hostName ,hostIP , portNumber);
            Future<AsynchronousSocketChannel> acceptFuture = serverSocket.accept();
            AsynchronousSocketChannel socketChannel = null;
            try {
                socketChannel = acceptFuture.get();
                if (shutdown) {
                    LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] [" + hostName + "] BrokerServer is shutdown.");
                    return;
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught " + e.getClass() + " Exception : " + e.getMessage());
            }

            //checking if the socketChannel is valid.
            if ((socketChannel != null) && (socketChannel.isOpen())) {
                try {
                    LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [CONNECTION REQUEST] Connection Established with " + socketChannel.getRemoteAddress().toString());
                } catch (IOException e) {
                    LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught " + e.getClass() + " Exception : " + e.getMessage());
                }
                Connection newConnection = null;
                newConnection = new Connection(socketChannel);
                LOGGER.debug("[Thread Id : " +Thread.currentThread().getId() + "] [REQUEST PROCESSOR] Connection given to the requestProcessor");
                // give this connection to requestProcessor
                RequestProcessor requestProcessor = new RequestProcessor(newConnection);
                threadPool.execute(requestProcessor);
            }
        }
    }
}
