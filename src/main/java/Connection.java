import model.Constants;

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
//    private String hostName;
    protected AsynchronousSocketChannel connectionSocket;
    private Future<Integer> incomingMessage;
    private ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    private Queue<byte[]> messageQueue = new LinkedList<>();

    /**
     * Constructor to initialise class attributes.
     * @param connectionSocket
     */
    public Connection(AsynchronousSocketChannel connectionSocket) {
//        this.hostName = hostName;
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
//                System.out.printf("\n[R] [THREAD ID : %s] Read successful !!! \n" , Thread.currentThread().getId());

            } else {
                //System.out.printf("\n[R] [THREAD ID : %s] End of Stream ... No more data !!! \n" , Thread.currentThread().getId());
            }
        } catch (TimeoutException e) {
//            System.out.printf("\n[R] [THREAD ID : %s] TIMEOUT encountered \n" , Thread.currentThread().getId());
            return messageQueue.poll();
        } catch (InterruptedException e) {
            System.out.printf("\n[Thread Id : %s] Execution Interrupted.\n", Thread.currentThread().getId());
        } catch (ExecutionException e) {
            System.out.printf("\n[Thread Id : %s] SOURCE has closed the Connection !!!", Thread.currentThread().getId());
            try {
                this.connectionSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
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
                System.out.printf("\n[Thread Id : %s] Execution Interrupted.\n", Thread.currentThread().getId());
                return false;
            } catch (ExecutionException e) {
                System.out.println("\nDESTINATION has closed the Connection !!!");
                System.out.printf("\n[Thread Id : %s] SOURCE has closed the Connection !!!", Thread.currentThread().getId());
                return false;
            }
            buffer.clear();
            return true;
        }
        return false;
    }
}
