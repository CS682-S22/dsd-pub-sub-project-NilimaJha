import customeException.ConnectionClosedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * class keeps information of broker.
 * @author nilimajha
 */
public class BrokerInfo {
    private static final Logger logger = LogManager.getLogger(BrokerInfo.class);
    private String brokerName;
    private int brokerId;
    private String brokerIP;
    private int brokerPort;
    private boolean isLeader;
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
     * send message over the connection
     */
    public void sendHeartbeat(byte[] message) {
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
     */
    public void sendData(byte[] message) {
        dataConnectionLock.writeLock().lock();
        try {
            connection.send(message);
        } catch (ConnectionClosedException e) {
            logger.info("\n" + e.getMessage());
            connection.closeConnection();
        }
        sendOverConnectionLock.writeLock().unlock();
    }
}
