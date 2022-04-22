package model;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.ReplicateSuccessACK;
import util.Constants;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class keeps information of broker.
 * @author nilimajha
 */
public class BrokerInfo {
    private static final Logger logger = LogManager.getLogger(BrokerInfo.class);
    private String brokerName;
    private int brokerId;
    private String brokerIP;
    private int brokerPort;
    private boolean isLeader;
    private boolean catchupMode = true;
    private Connection connection;
    private Connection dataConnection;
    private final ReentrantReadWriteLock sendOverConnectionLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock dataConnectionLock = new ReentrantReadWriteLock();

    /**
     * Constructor
     * @param brokerName
     * @param brokerId
     * @param brokerIP
     * @param brokerPort
     */
    public BrokerInfo(String brokerName, int brokerId, String brokerIP, int brokerPort) {
        this.brokerName = brokerName;
        this.brokerId = brokerId;
        this.brokerIP = brokerIP;
        this.brokerPort = brokerPort;
        this.isLeader = false;
        this.catchupMode = true;
    }

    /**
     * Constructor
     * @param brokerName
     * @param brokerIP
     * @param brokerPort
     */
    public BrokerInfo(String brokerName, String brokerIP, int brokerPort) {
        this.brokerName = brokerName;
        this.brokerIP = brokerIP;
        this.brokerPort = brokerPort;
    }

    /**
     * getter for attribute brokerName.
     * @return brokerName
     */
    public String getBrokerName() {
        return this.brokerName;
    }

    /**
     * getter for attribute brokerId.
     * @return brokerId
     */
    public int getBrokerId() {
        return this.brokerId;
    }

    /**
     * getter for attribute brokerIP.
     * @return brokerIP
     */
    public String getBrokerIP() {
        return this.brokerIP;
    }

    /**
     * getter for attribute brokerPort.
     * @return brokerPort
     */
    public int getBrokerPort() {
        return this.brokerPort;
    }

    /**
     * getter for attribute sync.
     * @return true/false
     */
    public boolean isInCatchupMode() {
        return catchupMode;
    }

    /**
     * getter for attribute isLeader.
     * @return isLeader
     */
    public boolean isLeader() {
        return isLeader;
    }

    /**
     * setter for attribute brokerId
     * @param id
     */
    public void setBrokerId(int id) {
        this.brokerId = id;
    }

    /**
     * setter for attribute isLeader
     * @param isLeader
     */
    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    /**
     * setter for attribute connection
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * setter for attribute dataConnection
     * @param dataConnection
     */
    public void setDataConnection(Connection dataConnection) {
        this.dataConnection = dataConnection;
    }

    /**
     * setter for attribute sync
     * @param catchupMode
     */
    public void setCatchupMode(boolean catchupMode) {
        this.catchupMode = catchupMode;
    }

    /**
     * send message over the connection
     */
    public void sendOverHeartbeatConnection(byte[] message) {
        sendOverConnectionLock.writeLock().lock();
        try {
            connection.send(message);
        } catch (ConnectionClosedException e) {
            logger.info(e.getMessage());
            connection.closeConnection();
        }
        sendOverConnectionLock.writeLock().unlock();
    }

    /**
     * send message over the dataConnection
     * mostly used for the replication purpose.
     */
    public void sendOverDataConnection(byte[] message, long expectedAckNumber) {
        dataConnectionLock.writeLock().lock();
        if (dataConnection != null && dataConnection.isConnected()) {
            int retries = 0;
            boolean sentSuccessful = false;
            logger.info("[ThreadId : " + Thread.currentThread().getId() + "] Sending data to broker with Id " + brokerId + " over DataConnection.");
            while (!sentSuccessful && retries < Constants.MAX_RETRIES) {
                try {
                    dataConnection.send(message);
                    // receive ack
                    byte[] receivedACK = dataConnection.receive();
                    if (receivedACK != null) {
                        Any any = Any.parseFrom(receivedACK);
                        if (any.is(ReplicateSuccessACK.ReplicateSuccessACKDetails.class)) {
                            ReplicateSuccessACK.ReplicateSuccessACKDetails replicateSuccessACK =
                                    any.unpack(ReplicateSuccessACK.ReplicateSuccessACKDetails.class);
                            if (replicateSuccessACK.getAckNum() == expectedAckNumber) {
                                sentSuccessful = true;
                                break;
                            }
                            retries++;
                        }
                    } else {
                        retries++;
                    }
                } catch (ConnectionClosedException e) {
                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] " + e.getMessage());
                    dataConnection.closeConnection();
                    retries++;
                } catch (InvalidProtocolBufferException e) {
                    logger.error("\n[ThreadId : " + Thread.currentThread().getId() + "] InvalidProtocolBufferException occurred while decoding receivedAck for replication message. Error Message : " + e.getMessage());
                }
            }
        }
        dataConnectionLock.writeLock().unlock();
    }

    /**
     * send message over the dataConnection
     * used for sending election messages or sync pull requests etc.
     * @param message
     */
    public void sendOverDataConnection(byte[] message) {
        dataConnectionLock.writeLock().lock();
        if (dataConnection != null && dataConnection.isConnected()) {
            try {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Sending data to broker with Id " + brokerId + " over DataConnection.");
                dataConnection.send(message);
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] " + e.getMessage());
                dataConnection.closeConnection();
            }
        }
        dataConnectionLock.writeLock().unlock();
    }

    /**
     * receives data over connection and returns the receivedMessage.
     * if connection is closed or null or connection gets closed in between then return null.
     * @return byte[] message / null
     */
    public byte[] receiveOverDataConnection() {
        dataConnectionLock.writeLock().lock();
        byte[] receivedMessage = null;
        if (dataConnection != null && dataConnection.isConnected()) {
            boolean dataReceived = false;
            try {
                while (!dataReceived) {
                    receivedMessage = dataConnection.receive();
                    if (receivedMessage != null) {
                        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received Data from broker with Id " + brokerId + " over DataConnection.");
                        dataReceived = true;
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] " + e.getMessage());
                dataConnection.closeConnection();
            }
        }
        dataConnectionLock.writeLock().unlock();
        return receivedMessage;
    }

    /**
     * method returns the status of the dataConnection between two broker.
     * @return true/false
     */
    public boolean isDataConnectionConnected() {
        return (dataConnection != null && dataConnection.isConnected());
    }
}
