import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

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
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Connection.class);
    protected AsynchronousSocketChannel connectionSocket;
    private Future<Integer> incomingMessage;
    private ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    private Queue<byte[]> messageQueue = new LinkedList<>();

    /**
     * Constructor to initialise class attributes.
     * @param connectionSocket
     */
    public Connection(AsynchronousSocketChannel connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.incomingMessage = this.connectionSocket.read(buffer);
    }

    /**
     * returns the message received over
     * the channelSocket in array of byte.
     * @return byte[] message
     */
    public byte[] receive() {
        if (!messageQueue.isEmpty()) {
            return messageQueue.poll();
        }

        byte[] messageByte = null;
        int totalDataToRead = 0;

        if (this.incomingMessage == null) {
            this.incomingMessage = connectionSocket.read(buffer);
        }
        try {
            int readHaveSomething = this.incomingMessage.get(Constants.READ_TIMEOUT_TIME, TimeUnit.MILLISECONDS);
            boolean readIsDone = this.incomingMessage.isDone();

            if (readHaveSomething != -1 && readIsDone) {
                this.incomingMessage = null;
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
            LOGGER.error("[Thread Id : "+ Thread.currentThread().getId() + "] Caught InterruptedException.");
        } catch (ExecutionException e) {
            LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] SOURCE has closed the Connection !!!");
            System.out.printf("\n[Thread Id : %s] SOURCE has closed the Connection !!!\n", Thread.currentThread().getId());
            try {
                this.connectionSocket.close();
            } catch (IOException ex) {
                LOGGER.error("Caught IOException while closing connection socket : " + ex);
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
    public boolean send(byte[] message) {
        if (connectionSocket.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(message.length + 10);
            buffer.putInt(message.length); //size of the next message.
            buffer.put(message); //actual message
            buffer.flip();

            Future result = connectionSocket.write(buffer);
            try {
                result.get();
            } catch (InterruptedException e) {
                LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "] Caught InterruptedException : " + e);
                return false;
            } catch (ExecutionException e) {
                LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] DESTINATION has closed the Connection !!!");
                System.out.printf("\n[Thread Id : %s] DESTINATION has closed the Connection !!!\n", Thread.currentThread().getId());
                return false;
            }
            buffer.clear();
            return true;
        }
        return false;
    }
}
