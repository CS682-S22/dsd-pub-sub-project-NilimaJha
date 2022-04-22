package model;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.RequestBrokerInfo;
import proto.RequestLeaderAndMembersInfo;
import proto.ResponseLeaderInfo;
import proto.ResponseRandomBrokerInfo;
import util.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Parent class for Producer and Consumer class.
 * @author nilimajha
 */
public class Node {
    private static final Logger logger = LogManager.getLogger(Node.class);
    protected String name;
    protected String nodeType;
    protected BrokerInfo thisBrokerInfo;
    protected String loadBalancerName;
    protected String loadBalancerIP;
    protected int loadBalancerPort;
    protected String leaderBrokerName;
    protected int leaderBrokerId = 0;
    protected String leaderBrokerIP = null;
    protected int leaderBrokerPort;
    protected List<ByteString> memberList;
    protected Connection connection;
    protected Connection loadBalancerConnection;
    protected volatile boolean connected;
    private Timer timer;
    private final Object waitObj = new Object();

    /**
     * constructor for producer class attributes
     * @param loadBalancerIP loadBalancer.LoadBalancer IP
     * @param loadBalancerPort loadBalancer.LoadBalancer port
     */
    public Node (String name, String nodeType, String loadBalancerName, String loadBalancerIP, int loadBalancerPort) {
        this.name = name;
        this.nodeType = nodeType;
        this.loadBalancerName = loadBalancerName;
        this.loadBalancerIP = loadBalancerIP;
        this.loadBalancerPort = loadBalancerPort;
    }

    /**
     * constructor for producer class attributes
     * @param loadBalancerIP loadBalancer.LoadBalancer IP
     * @param loadBalancerPort loadBalancer.LoadBalancer port
     */
    public Node (String name, String nodeType, String thisBrokerIP, int thisBrokerPort, String loadBalancerName,
                 String loadBalancerIP, int loadBalancerPort) {
        this.name = name;
        this.nodeType = nodeType;
        this.thisBrokerInfo = new BrokerInfo(name, thisBrokerIP, thisBrokerPort);
        this.loadBalancerName = loadBalancerName;
        this.loadBalancerIP = loadBalancerIP;
        this.loadBalancerPort = loadBalancerPort;
    }

    /**
     * Timer Task thread start method.
     */
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                notifyThread();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, Constants.FLUSH_FREQUENCY);
    }

    /**
     * method that will be executed by timer task thread to notify thread waiting on onj waitObj.
     */
    public void notifyThread() {
        timer.cancel();
        synchronized (waitObj) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Notifying the thread about timeout.");
            waitObj.notify();
        }
    }

    /**
     * establishes the connection with loadBalancer.
     */
    public void connectToLoadBalancer() throws ConnectionClosedException {
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
            InetSocketAddress brokerAddress = new InetSocketAddress(loadBalancerIP, loadBalancerPort);
//            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Connecting To Load Balancer.");
            Future<Void> futureSocket = clientSocket.connect(brokerAddress);
            try {
                futureSocket.get();
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Connected to Load Balancer.");
                loadBalancerConnection = new Connection(clientSocket); //connection established with the load balancer.
            }  catch (InterruptedException | ExecutionException e) {
                logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] Exception occurred while connecting to LoadBalancer. Error Message : " + e.getMessage() + " e.cause : " + e.getCause());
                throw new ConnectionClosedException("No Host running on the given IP & port!!!");
            }
        } catch (IOException e) {
            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] IOException occurred while connecting to broker.Broker.");
        }
    }

    /**
     *
     */
    public void getLeaderAndMembersInfo() throws ConnectionClosedException {
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Getting leader info from loadBalancer.");
        if (loadBalancerConnection != null && loadBalancerConnection.isConnected()) {
            int messageId = 1;
            boolean responseReceived = false;
            while (!responseReceived) {
                if (nodeType.equals(Constants.CONSUMER)) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Requesting random broker info from the LoadBalancer.");
                    loadBalancerConnection.send(getRequestRandomBrokerInfoMessage(messageId));
//                    logger.info("\nRequest sent for Leader info and Membership table.");
                } else {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Requesting leader's info from the LoadBalancer.");
                    loadBalancerConnection.send(getRequestLeaderInfoMessage(messageId));
//                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Request sent for Leader info and Membership table.");
                }

                byte[] receivedResponse = null;
                try {
                    receivedResponse = loadBalancerConnection.receive();
                    if (receivedResponse != null) {
                        messageId += 1;
                        responseReceived = true;
                        try {
                            Any any = Any.parseFrom(receivedResponse);
                            if (any.is(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.class)) {
                                ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails responseLeaderInfoDetails =
                                        any.unpack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.class);
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Response Received of Type ResponseLeaderInfo. Leader Info Available : "
                                        + responseLeaderInfoDetails.getInfoAvailable());
                                if (responseLeaderInfoDetails.getInfoAvailable()) {
                                    // getting all the info about current leader.
                                    leaderBrokerName = responseLeaderInfoDetails.getLeaderName();
                                    leaderBrokerIP = responseLeaderInfoDetails.getLeaderIP();
                                    leaderBrokerPort = responseLeaderInfoDetails.getLeaderPort();
                                    leaderBrokerId = responseLeaderInfoDetails.getLeaderID();
                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] leaderBrokerId received from load balancer : " + leaderBrokerId + " leaderIp : " + leaderBrokerIP + " leaderPort : " + leaderBrokerPort + " leaderName : " + leaderBrokerName);
                                } else if (!nodeType.equals(Constants.BROKER)) {
                                    synchronized (waitObj) {
                                        startTimer();
                                        try {
                                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Leader's info is not available at loadBalancer.LoadBalancer yet. Waiting for sometime...");
                                            waitObj.wait();
                                        } catch (InterruptedException e) {
                                            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                                        }
                                    }
                                }
                                if (nodeType.equals(Constants.BROKER)) {
                                    memberList = responseLeaderInfoDetails.getMembersList();
                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] MemberList size : " + memberList.size());
                                    if (thisBrokerInfo.getBrokerId() == 0) {
                                        thisBrokerInfo.setBrokerId(responseLeaderInfoDetails.getBrokerId());
                                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] This broker Id : " + thisBrokerInfo.getBrokerId());
                                    }
                                }
                            } else if (any.is(ResponseRandomBrokerInfo.ResponseRandomBrokerInfoDetails.class)) {
                                ResponseRandomBrokerInfo.ResponseRandomBrokerInfoDetails randomBrokerInfoDetails =
                                        any.unpack(ResponseRandomBrokerInfo.ResponseRandomBrokerInfoDetails.class);
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Response Received of Type ResponseLeaderInfo. Leader Info Available : "
                                        + randomBrokerInfoDetails.getInfoAvailable());
                                if (randomBrokerInfoDetails.getInfoAvailable()) {
                                    // getting all the info about the broker.
                                    leaderBrokerName = randomBrokerInfoDetails.getBrokerName();
                                    leaderBrokerIP = randomBrokerInfoDetails.getBrokerIP();
                                    leaderBrokerPort = randomBrokerInfoDetails.getBrokerPort();
                                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] BrokerId received from load balancer : " + leaderBrokerId + " brokerIp : " + leaderBrokerIP + " brokerPort : " + leaderBrokerPort + " brokerName : " + leaderBrokerName);
                                } else {
                                    responseReceived = false;
                                    synchronized (waitObj) {
                                        startTimer();
                                        try {
                                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Broker info is not available at loadBalancer. Waiting for sometime...");
                                            waitObj.wait();
                                        } catch (InterruptedException e) {
                                            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InterruptedException occurred. Error Message : " + e.getMessage());
                                        }
                                    }
                                }

                            }
                        } catch (InvalidProtocolBufferException e) {
                            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding message send by loadBalancer.");
                        }
                    }
                } catch (ConnectionClosedException e) {
                    logger.info(e.getMessage());
                    loadBalancerConnection.closeConnection();
                }
            }
        }
//        else {
//            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Not connected with loadBalancer.");
//            connectToLoadBalancer();
//        }

    }

    /**
     * creates appropriate Leader request message to the loadBalancer.LoadBalancer.
     * @return requestLeaderInfoMessage
     */
    public byte[] getRequestLeaderInfoMessage(int messageId) {
        boolean assignBrokerId = false;
        Any any;
//        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] NodeType : " + nodeType);
        if (nodeType.equals(Constants.BROKER) && thisBrokerInfo.getBrokerId() == 0) {
            assignBrokerId = true;
            any = Any.pack(RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails.newBuilder()
                    .setMessageId(messageId)
                    .setRequestSenderType(nodeType)
                    .setRequestSenderName(name)
                    .setBrokerIP(thisBrokerInfo.getBrokerIP())
                    .setBrokerPort(thisBrokerInfo.getBrokerPort())
                    .setAssignBrokerId(assignBrokerId)
                    .build());
        } else {
            any = Any.pack(RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails.newBuilder()
                    .setMessageId(messageId)
                    .setRequestSenderType(nodeType)
                    .setRequestSenderName(name)
                    .setAssignBrokerId(assignBrokerId)
                    .build());
        }
        return any.toByteArray();
    }

    /**
     * creates appropriate Leader request message to the loadBalancer.LoadBalancer.
     * @return requestLeaderInfoMessage
     */
    public byte[] getRequestRandomBrokerInfoMessage(int messageId) {
        Any any;
//        logger.info("\n NodeType : " + nodeType);
        any = Any.pack(RequestBrokerInfo.RequestBrokerInfoDetails.newBuilder()
                .setMessageId(messageId)
                .setRequestSenderType(nodeType)
                .setRequestSenderName(name)
                .build());
        return any.toByteArray();
    }

    /**
     * method that connects to the broker and saves the connection object.
     * @return  true/false
     */
    public boolean connectToBroker() throws ConnectionClosedException {
        AsynchronousSocketChannel clientSocket = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
            InetSocketAddress brokerAddress = new InetSocketAddress(leaderBrokerIP, leaderBrokerPort);
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Connecting To Broker " + leaderBrokerIP + " " + leaderBrokerPort);
            Future<Void> futureSocket = clientSocket.connect(brokerAddress);
            try {
                futureSocket.get();
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Connection Successful");
                connection = new Connection(clientSocket);
                connected = true;
            } catch (InterruptedException | ExecutionException e) {
                logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] Exception occurred while connecting to broker. Error Message : " + e.getMessage());
                connected = false;
                throw new ConnectionClosedException("No host is running on the given IP and Port.");
            }
        } catch (IOException e) {
            connected = false;
            logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] IOException occurred while connecting to broker.Broker. Error Message : " + e.getMessage());
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
     * return the status of connection with broker of this node.
     * @return true/false.
     */
    public boolean connectedToBroker() {
        return connected;
    }

    /**
     * closes the loadBalancerConnection.
     */
    public void closeLoadBalancerConnection() {
        if (loadBalancerConnection != null) {
            loadBalancerConnection.closeConnection();
            loadBalancerConnection = null;
        }
    }

    /**
     *
     * @return
     */
    public boolean resetLeaderBrokerInfo() {
        logger.info("\nResetting the LeaderInfo.");
        this.leaderBrokerId = 0;
        this.leaderBrokerIP = null;
        this.leaderBrokerPort = 0;
        this.leaderBrokerName = null;
        return true;
    }
}
