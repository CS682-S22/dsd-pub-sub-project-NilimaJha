package loadBalancer;

import connection.Connection;
import model.LoadBalancerDataStore;
import util.Constants;
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
 * LoadBalancer class which has a ServerSocket running on a give IP and Port and
 * accepts requests from Broker /Producer /Consumer.
 * @author nilimajha
 */
public class LoadBalancer {
    private static final Logger logger = LogManager.getLogger(LoadBalancer.class);
    private boolean shutdown = false;
    private String loadBalancerName;
    private String loadBalancerIP;
    private int loadBalancerPort;
    private LoadBalancerDataStore loadBalancerDataStore = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(Constants.LOAD_BALANCER_THREAD_POOL_SIZE);

    /**
     * Constructor
     * @param loadBalancerName Name of this broker
     * @param loadBalancerIP Ip of this broker
     * @param loadBalancerPort Port on which this broker is running
     */
    public LoadBalancer(String loadBalancerName, String loadBalancerIP, int loadBalancerPort) {
        this.loadBalancerName = loadBalancerName;
        this.loadBalancerIP = loadBalancerIP;
        this.loadBalancerPort = loadBalancerPort;
        this.loadBalancerDataStore = LoadBalancerDataStore.getLoadBalancerDataStore();
    }

    /**
     * opens a serverSocket and keeps listening for
     * new connection request from producer or consumer or Broker.
     */
    public void startLoadBalancer() {
        AsynchronousServerSocketChannel serverSocket = null;
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(loadBalancerIP, loadBalancerPort));
            // keeps on running when shutdown is false
            while (!shutdown) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] [INFO] " + loadBalancerName +
                        " Server is listening on IP : " + loadBalancerIP + " & Port : " + loadBalancerPort);
                Future<AsynchronousSocketChannel> acceptFuture = serverSocket.accept();
                AsynchronousSocketChannel socketChannel = null;
                try {
                    socketChannel = acceptFuture.get();
                    if (shutdown) {
                        return;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] Exception while establishing " +
                            "connection. Error Message : " + e.getMessage());
                }

                //checking if the socketChannel is valid.
                if ((socketChannel != null) && (socketChannel.isOpen())) {
                    Connection connection = null;
                    connection = new Connection(socketChannel);
                    // give this connection to handler
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] New connection established.");
                    LBHandler handler = new LBHandler(connection, loadBalancerName, loadBalancerDataStore);
                    threadPool.execute(handler);
                }
            }
        } catch (IOException e) {
            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] IOException while opening serverSocket" +
                    " connection. Error Message : " + e.getMessage());
        }
    }
}
