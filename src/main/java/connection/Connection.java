package connection;

import customeException.ConnectionClosedException;
import util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A wrapper class to wrap over connectionSockets.
 * Class to represent connection between two hosts.
 * and stores related information.
 * @author nilimajha
 */
public class Connection {
    private static final Logger logger = LogManager.getLogger(Connection.class);
    private AsynchronousSocketChannel connectionSocket;
    private boolean isConnected;
    private Future<Integer> incomingMessage;
    private ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    private Queue<byte[]> messageQueue = new LinkedList<>();

    /**
     * Constructor to initialise class attributes.
     * @param connectionSocket
     */
    public Connection(AsynchronousSocketChannel connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.isConnected = true;
        this.incomingMessage = this.connectionSocket.read(buffer);
    }

    /**
     * returns the message received over
     * the channelSocket in array of byte.
     * @return byte[] message
     */
    public byte[] receive() throws ConnectionClosedException {
        if (!messageQueue.isEmpty()) {
            return messageQueue.poll();
        }

        byte[] messageByte = null;
        int totalDataToRead = 0;

        if (incomingMessage == null) {
            incomingMessage = connectionSocket.read(buffer);
        }
        if (isConnected) {
            try {
                int readHaveSomething = incomingMessage.get(Constants.READ_TIMEOUT_TIME, TimeUnit.MILLISECONDS);
                boolean readIsDone = incomingMessage.isDone();

                if (readHaveSomething != -1 && readIsDone) {
                    incomingMessage = null;
                    totalDataToRead = buffer.position();
                    while (buffer.position() > 4) {
                        buffer.flip();
                        int nextMessageSize = buffer.getInt();
                        if (totalDataToRead >=  4 + nextMessageSize ) {
                            messageByte = new byte[nextMessageSize];
                            buffer.get(messageByte, 0, nextMessageSize);
                            messageQueue.add(messageByte);
                            buffer.compact();
                            totalDataToRead = buffer.position();
                        } else {
                            buffer.position(0);
                            buffer.compact();
                            break;
                        }
                    }
                }
            } catch (TimeoutException e) {
                return messageQueue.poll();
            } catch (InterruptedException e) {
                logger.error("\nInterruptedException while establishing connection. Error Message : " + e.getMessage());
            } catch (ExecutionException e) {
                if (e.getCause().toString().equals(Constants.BROKEN_PIPE)) {
                    logger.info("\nconnection.Connection is closed by other host!!!");
                    isConnected = false;
                    throw new ConnectionClosedException("connection.Connection is closed by other host!!!");
                }
                try {
                    this.connectionSocket.close();
                } catch (IOException ex) {
                    logger.error("\nIOException while establishing connection. Error Message : " + e.getMessage());
                }
            }
        }
        return messageQueue.poll();
    }

    /**
     * methods sends byte message over the channelSocket.
     * and returns true on success and false if not successful.
     * @param message
     * @return true/false
     */
    public boolean send(byte[] message) throws ConnectionClosedException {
        if (isConnected && connectionSocket.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(message.length + 10);
            buffer.putInt(message.length); //size of the next message.
            buffer.put(message); //actual message
            buffer.flip();

            Future result = connectionSocket.write(buffer);
            try {
                result.get();
            } catch (InterruptedException e) {
                logger.error("\nInterruptedException while writing on the connectionSocket. Error Message : "
                        + e.getMessage());
                return false;
            } catch (ExecutionException e) {
                if (e.getCause().toString().equals(Constants.BROKEN_PIPE)) {
                    isConnected = false;
                    throw new ConnectionClosedException("connection.Connection is closed by other host!!!");
                } else {
                    logger.error("\nExecutionException occurred. Error Message : " + e.getMessage());
                }
                return false;
            }
            buffer.clear();
            return true;
        } else {
            logger.info("connection.Connection is not connected............");
        }
        return false;
    }

    /**
     * returns the status of connectionSocket.
     * @return true/false
     */
    public boolean connectionIsOpen() {
        return connectionSocket.isOpen();
    }

    /**
     * closes the connectionSocket
     * @return true/false
     */
    public boolean closeConnection() {
        try {
            isConnected = false;
            connectionSocket.close();
        } catch (IOException e) {
            logger.info("\nIOException occurred while closing the connectionSocket. Error Message : " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * return the value of the boolean variable isConnected.
     * @return isConnected
     */
    public boolean isConnected() {
        return isConnected;
    }
}
