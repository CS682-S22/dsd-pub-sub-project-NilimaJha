package broker;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import model.MembershipTable;
import model.Node;
import util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.InitialMessage;
import proto.InitialSetupDone;
import proto.MembersInfo;
import proto.UpdateLeaderInfo;
import util.Utility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * broker.Broker class that keeps a serverSocket open to
 * listen for new connection request from producer and consumer.
 * Creates new broker.RequestProcessor object for each incoming request.
 * @author nilimajha
 */
public class Broker extends Node implements Runnable {
    private static final Logger logger = LogManager.getLogger(Broker.class);
    private boolean shutdown = false;
    private List<String> catchupTopics;
    private MembershipTable membershipTable;
    private HeartBeatModule heartBeatModule;
    private ExecutorService threadPool = Executors.newFixedThreadPool(Constants.BROKER_THREAD_POOL_SIZE);

    /**
     * Constructor
     * @param brokerName Name of this broker
     * @param brokerIP Ip of this broker
     * @param brokerPort Port on which this broker is running
     */
    public Broker(String brokerName, String brokerIP, int brokerPort, String loadBalancerName, String loadBalancerIP, int loadBalancerPort) {
        super(brokerName, Constants.BROKER, brokerIP, brokerPort, loadBalancerName, loadBalancerIP, loadBalancerPort);
        this.membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
        this.heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, loadBalancerIP, loadBalancerPort);

    }

    /**
     *
     */
    public void sendUpdateLeaderMessageToLB() {
        int messageId = 0;
        Any updateLeaderMessage = Any.pack(UpdateLeaderInfo.UpdateLeaderInfoDetails.newBuilder()
                .setMessageId(messageId)
                .setRequestSenderType(Constants.BROKER)
                .setBrokerName(name)
                .setBrokerId(thisBrokerInfo.getBrokerId())
                .build());
        boolean leaderUpdated = false;
        while (!leaderUpdated) {
            try {
                loadBalancerConnection.send(updateLeaderMessage.toByteArray());
                byte[] receivedUpdateResponse = loadBalancerConnection.receive();
                if (receivedUpdateResponse != null) {
                    leaderBrokerName = thisBrokerInfo.getBrokerName();
                    leaderBrokerIP = thisBrokerInfo.getBrokerIP();
                    leaderBrokerPort = thisBrokerInfo.getBrokerPort();
                    leaderBrokerId = thisBrokerInfo.getBrokerId();
                    leaderUpdated = true;
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                loadBalancerConnection.closeConnection();
            }
        }
    }

    /**
     *
     */
    public void updateMembershipTable() {
        logger.info("\nmemberList Size : " + memberList.size());
        for (ByteString eachMemberByteString : memberList) {
            try {
                MembersInfo.MembersInfoDetails membersInfoDetails = MembersInfo.MembersInfoDetails
                        .parseFrom(eachMemberByteString.toByteArray());

                BrokerInfo eachMember = new BrokerInfo(membersInfoDetails.getMemberName(),
                        membersInfoDetails.getMemberId(), membersInfoDetails.getMemberIP(), membersInfoDetails.getMemberPort());
                logger.info("\nEach memberId : " + eachMember.getBrokerName() +
                        " Each memberName : " + eachMember.getBrokerName() +
                        " Each memberIP : " + eachMember.getBrokerIP() +
                        " Each memberPort : " + eachMember.getBrokerPort());

                if (eachMember.getBrokerId() != thisBrokerInfo.getBrokerId()) {
                    // connecting with each member
                    int retries = 0;
                    Connection connection = null;
                    try {
                        connection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                    } catch (ConnectionClosedException e) {
                        logger.info(e.getMessage());
                        if (connection != null) {
                            connection.closeConnection();
                        }
                    }
                    while (connection == null && retries < Constants.MAX_RETRIES) {
                        try {
                            connection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                        } catch (ConnectionClosedException e) {
                            logger.info(e.getMessage());
                        }
                        retries++;
                    }

                    if (connection != null) {
                        eachMember.setConnection(connection);
                        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] [Connected to Member with member Id : " + eachMember.getBrokerId() + "]");
                        sendInitialMessageToMember(connection, Constants.HEARTBEAT_CONNECTION); //sending initial setup message.
                        membershipTable.addMember(eachMember.getBrokerId(), eachMember);
                        heartBeatModule.updateHeartBeat(eachMember.getBrokerId());
                        RequestProcessor requestProcessor = new RequestProcessor(thisBrokerInfo.getBrokerName(),
                                connection, thisBrokerInfo,
                                Constants.BROKER,
                                eachMember,
                                Constants.HEARTBEAT_CONNECTION, loadBalancerIP, loadBalancerPort);
                        threadPool.execute(requestProcessor);
                        retries = 0;
                        Connection dataConnection =  null;
                        try {
                            dataConnection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                        } catch (ConnectionClosedException e) {
                            logger.info(e.getMessage());
                            connection.closeConnection();
                        }
                        while (dataConnection == null && retries < Constants.MAX_RETRIES) {
                            try {
                                dataConnection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                            } catch (ConnectionClosedException e) {
                                logger.info(e.getMessage());
                                connection.closeConnection();
                            }
                            retries++;
                        }
                        if (dataConnection != null) {
                            sendInitialMessageToMember(dataConnection, Constants.DATA_CONNECTION);
                            membershipTable.addDataConnectionToMember(eachMember.getBrokerId(), dataConnection);
                        }
                    }
                    logger.info("\n eachMember.getBrokerId : " + eachMember.getBrokerId() + " LeaderId : " + membershipTable.getLeaderId());
                    if (eachMember.getBrokerId() == membershipTable.getLeaderId() && connection != null) {
                        //set up CatchUp Connection.
                        retries = 0;
                        logger.info("\n [ThreadId : " + Thread.currentThread().getId() + "] Connecting to member with id : " + eachMember.getBrokerId() + " Connection Type : " + Constants.CATCHUP_CONNECTION);
                        Connection catchupConnection = null;
                        try {
                            catchupConnection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                        } catch (ConnectionClosedException e) {
                            logger.info(e.getMessage());
                        }
                        while (catchupConnection == null && retries < Constants.MAX_RETRIES) {
                            try {
                                catchupConnection = Utility.establishConnection(eachMember.getBrokerIP(), eachMember.getBrokerPort());
                            } catch (ConnectionClosedException e) {
                                logger.info(e.getMessage());
                            }
                            retries++;
                        }
                        if (catchupConnection != null) {
                            logger.info("\n [ThreadId : " + Thread.currentThread().getId() +
                                    "] Connecting to member with id : " + eachMember.getBrokerId() +
                                    " Connection Type : " + Constants.CATCHUP_CONNECTION);
                            sendInitialMessageToMember(catchupConnection, Constants.CATCHUP_CONNECTION);
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() +
                                    "] Initial Setup done for connection type : " + Constants.CATCHUP_CONNECTION);
                            logger.info("\n catchupTopic details, : " + catchupTopics + ": catchupTopic.size : " + catchupTopics.size());
                            if (catchupTopics != null && catchupTopics.size() > 0) {
                                CatchupModule catchup = new CatchupModule(thisBrokerInfo.getBrokerName(),
                                        catchupConnection, thisBrokerInfo,  loadBalancerIP, loadBalancerPort, eachMember.getBrokerName(), eachMember,
                                        Constants.CATCHUP_CONNECTION, catchupTopics);
                                threadPool.execute(catchup);
                            } else {
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Broker Is UpToDate.");
                                thisBrokerInfo.setCatchupMode(false); // this broker is up-to-date.
                                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Closing the CatchupConnection.");
                                catchupConnection.closeConnection();

                            }
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                logger.error("InvalidProtocolBufferException occurred while decoding member's " +
                        "info from list provided by load balancer. Error Message : " + e.getMessage());
            }
        }
    }

    /**
     *
     * @param connection
     */
    public void sendInitialMessageToMember(Connection connection, String typeOfConnection) {
        boolean initialSetupDone = false;
        int messageID = 0;
        Any any = Any.pack(InitialMessage.InitialMessageDetails.newBuilder()
                .setMessageId(messageID)
                .setConnectionSender(Constants.BROKER)
                .setName(thisBrokerInfo.getBrokerName())
                .setBrokerId(thisBrokerInfo.getBrokerId())
                .setBrokerIP(thisBrokerInfo.getBrokerIP())
                .setBrokerPort(thisBrokerInfo.getBrokerPort())
                .setConnectionType(typeOfConnection)
                .build());
        logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Sending InitialMessage of type " + typeOfConnection);
        while (!initialSetupDone) {
            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Sending InitialSetup Message to the member.");
            try {
                connection.send(any.toByteArray());
                byte[] receivedMessage = connection.receive();
                if (receivedMessage != null) {
                    try {
                        Any any1 = Any.parseFrom(receivedMessage);
                        if (any1.is(InitialSetupDone.InitialSetupDoneDetails.class)) {
                            InitialSetupDone.InitialSetupDoneDetails initialSetupDoneDetails =
                                    any1.unpack(InitialSetupDone.InitialSetupDoneDetails.class);
                            logger.info("\n[Thread Id : " + Thread.currentThread().getId() + "] Received InitialSetup Message ACK from the member. connection type : " + typeOfConnection);
                            if (typeOfConnection.equals(Constants.CATCHUP_CONNECTION)) {
                                catchupTopics = initialSetupDoneDetails.getTopicsList();
                            }
                            initialSetupDone = true;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.info("\nInvalidProtocolBufferException while decoding Ack for InitialSetupMessage.");
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n" + e.getMessage());

            }
        }
    }

    /**
     * connects to loadBalancer and gets the leader and member's info.
     * connects to all the member and updates its membership table.
     * it there is no member in the membership table then it registers itself as the leader.
     */
    public void initialSetup() {
        try {
            connectToLoadBalancer();
            getLeaderAndMembersInfo();
            membershipTable.updateLeader(leaderBrokerId);
            if (memberList.isEmpty() && leaderBrokerId == 0) {
                // this broker is the first broker in the membershipTable.
                //registering itself as Leader at loadBalancer.LoadBalancer
                logger.info("\nSetting this Broker Is Up-to-date.");
                thisBrokerInfo.setCatchupMode(false);
                sendUpdateLeaderMessageToLB();
                membershipTable.updateLeader(thisBrokerInfo.getBrokerId());
                thisBrokerInfo.setLeader(true);
            }
            closeLoadBalancerConnection();
            //update membership table
            updateMembershipTable();
            if (membershipTable.getMembershipInfo().size() == 0) {
                logger.info("\nNo member is active so registering itself as leader on loadBalancer.");

            }
        } catch (ConnectionClosedException e) {
            logger.info("\nException Occurred while connecting to load balancer. Error Message : " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * run opens a serverSocket and keeps listening for
     * new connection request from producer or consumer.
     * once it receives a connection request it creates a
     * connection object and hands it to the broker.RequestProcessor class object.
     */
    @Override
    public void run() {
        /**
         * connect to LB
         * get leader's and membership info.
         */
        threadPool.execute(this::initialSetup);
        AsynchronousServerSocketChannel serverSocket = null;

        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(thisBrokerInfo.getBrokerIP(), thisBrokerInfo.getBrokerPort()));
            // keeps on running when shutdown is false
            while (!shutdown) {
                logger.info("\n[broker.Broker : " + thisBrokerInfo.getBrokerName() +
                        " BrokerServer is listening on IP : " + thisBrokerInfo.getBrokerIP() +
                        " & Port : " + thisBrokerInfo.getBrokerPort());
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
                    logger.info("\nReceived new connection.Connection.");
                    RequestProcessor requestProcessor = new RequestProcessor(thisBrokerInfo.getBrokerName(),
                            newConnection, thisBrokerInfo, loadBalancerIP, loadBalancerPort);
                    threadPool.execute(requestProcessor);
                }
            }
        } catch (IOException e) {
            logger.error("\nIOException while opening serverSocket connection. Error Message : " + e.getMessage());
        }
    }

    /**
     * method to set id of the brokerInfo instance of this class.
     * @param id brokerId
     */
    public void setBrokerId(int id) {
        thisBrokerInfo.setBrokerId(id);
    }
}
