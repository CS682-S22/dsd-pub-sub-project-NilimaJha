package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to store all the data published for a topic.
 * @author nilimajha
 */
public class MessageInfo {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(MessageInfo.class);
    private ArrayList<Integer> flushedMessageOffset;
    private ArrayList<Integer> inMemoryMessageOffset;
    private ArrayList<byte[]> inMemoryMessage;
    private int lastOffSet = 0; // updated after adding each message
    private FileOutputStream fileWriter = null;
    private FileInputStream fileReader = null;
    private String topicSegmentFileName;
    private boolean topicIsAvailable = true;
    final ReentrantReadWriteLock lock1 = new ReentrantReadWriteLock(); // lock for inMemory data store
    final ReentrantReadWriteLock lock2 = new ReentrantReadWriteLock(); // lock for persistent storage and flushedMessageOffset ArrayList.
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

    /**
     * Constructor to initialise class attributes.
     * @param topic
     */
    public MessageInfo(String topic) {
        this.flushedMessageOffset = new ArrayList<>();
        this.inMemoryMessageOffset = new ArrayList<>();
        this.inMemoryMessage = new ArrayList<>();
        this.topicSegmentFileName = topic + ".log";
        threadPool.execute(this::flushIfNeeded);
        fileWriterInitializer(this.topicSegmentFileName);
        fileReaderInitializer(this.topicSegmentFileName);
    }

    /**
     * initialises the FileOutputStream named fileWriter of the class and deletes the file if already exist.
     * @param fileName
     */
    public void fileWriterInitializer(String fileName) {
        File segmentFile = new File(fileName);
        if(segmentFile.exists()){
            segmentFile.delete(); //deleting file if exist
        }

        try {
            fileWriter = new FileOutputStream(fileName, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialises the FileInputStream named fileReader of the class.
     * @param fileName
     */
    private void fileReaderInitializer(String fileName) {
        try {
            fileReader = new FileInputStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * appending new message published by publisher to the inMemory buffer ArrayList.
     * and if after adding new message buffer ArrayList is full then
     * flushing those inMemory message to the file on the disk and
     * making it available for the consumer.
     * @param message
     * @return true
     */
    public boolean addNewMessage(byte[] message) {
        // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        lock1.writeLock().lock();
        inMemoryMessageOffset.add(this.lastOffSet);
        inMemoryMessage.add(message);
        lastOffSet += message.length;
        if (inMemoryMessageOffset.size() == Constants.TOTAL_IN_MEMORY_MESSAGE_SIZE) {
            flushOnFile();
        }
        // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        lock1.writeLock().unlock();
        return true;
    }

    /**
     * writing inMemory data on the file using FileOutputStream named fileWriter.
     */
    public void flushOnFile() {
        // acquire write lock on the file and flushedMessageOffset ArrayList
        lock2.writeLock().lock();
        //flushing data on the file on file
        for (byte[] eachMessageByteArray : inMemoryMessage) {
            try {
                fileWriter.write(eachMessageByteArray);
            } catch (IOException e) {
                lock2.writeLock().unlock();
                e.printStackTrace();
            }
        }
        flushedMessageOffset.addAll(this.inMemoryMessageOffset);
        // clearing in-memory buffer of the published message.
        inMemoryMessageOffset.clear();
        inMemoryMessage.clear();
        System.out.printf("\n[Thread Id : %s] [Flushing in-memory data to file %s]\n", Thread.currentThread().getId(), topicSegmentFileName);
        LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [Flushing in-memory data to file " + topicSegmentFileName + "]");
        // realising write lock on the file and flushedMessageOffset ArrayList
        lock2.writeLock().unlock();
    }

    /**
     * reads 10 message from the offset given and returns it.
     * @param offSet
     * @return messageBatch
     */
    public ArrayList<byte[]> getMessage(int offSet) {
        lock2.readLock().lock();
        ArrayList<byte[]> messageBatch = null;
        int count = 0;
        int currentOffset = offSet;
        // get the current offset index in the flushedMessageOffset ArrayList
        int index = flushedMessageOffset.indexOf(offSet);
        // offset is not available
        if (index == -1 && currentOffset > flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {
            LOGGER.debug("[Thread Id : %s] [offset number %d is not available. Last Offset number available is %d]", Thread.currentThread().getId(), offSet, flushedMessageOffset.get(flushedMessageOffset.size() - 1));
            lock2.readLock().unlock();
            return messageBatch;
        } else if (index == -1) {
            LOGGER.debug("[Thread Id : %s] [offset number %d is not a valid offset number.]", Thread.currentThread().getId(), offSet);
            lock2.readLock().unlock();
            return messageBatch;
        } else {
            messageBatch = new ArrayList<>();
        }

        while (count < Constants.MESSAGE_BATCH_SIZE && currentOffset <= flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {
            // read one message at a time and append it on the messageBatch arrayList
            synchronized (this) { // making this block of code synchronised so that at a time only one thread can use FileInputStream named fileReader
                byte[] eachMessage = new byte[0];
                if (index == flushedMessageOffset.size() - 1) {
                    try {
                        fileReader.getChannel().position(currentOffset);
                        eachMessage = new byte[fileReader.available()]; // getting the size of the current last message.
                    } catch (IOException e) {
                        LOGGER.error("Caught IOException : %s", e);
                    }
                } else {
                    eachMessage = new byte[flushedMessageOffset.get(index + 1) - currentOffset];
                }

                try {
                    fileReader.getChannel().position(currentOffset);
                    fileReader.read(eachMessage);
                } catch (IOException e) {
                    LOGGER.error("Caught IOException : %s", e);
                }
                messageBatch.add(eachMessage);
                count++;
                if (index == flushedMessageOffset.size() - 1) {
                    currentOffset = flushedMessageOffset.get(index) + eachMessage.length;
                } else {
                    currentOffset = flushedMessageOffset.get(index + 1);
                }
                index++;
            }
        }
        lock2.readLock().unlock();
        return messageBatch;
    }

    /**
     * this method will keep running in a loop.
     * inside the loop it will first sleep for timeout amount of time,
     * then will wake up and flushes the in-memory data on to the file if needed.
     */
    private void flushIfNeeded() {
        while (topicIsAvailable) {
            try {
                LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [sleeping for 6000 milli sec]");
                Thread.sleep(Constants.FLUSH_FREQUENCY);
                LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] [Awake after 6000 ms]");
            } catch (InterruptedException e) {
                LOGGER.error("Caught InterruptedException : " + e);
            }
            // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList
            lock1.writeLock().lock();
            LOGGER.debug("[Thread Id : " + Thread.currentThread().getId() + "] Checking if flushing is needed [Total element in buffer : " + inMemoryMessageOffset.size() + "]");
            if (inMemoryMessageOffset.size() != 0) {
                flushOnFile();
            }
            // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
            lock1.writeLock().lock();
        }
    }

    /**
     * method will set the topicIsAvailable as false so that
     * the thread that is running and flushing the message onto the file if needed
     * after timeout can be stopped.
     */
    public void cancelTopic () {
        topicIsAvailable = false;
        threadPool.shutdown();
    }
}
