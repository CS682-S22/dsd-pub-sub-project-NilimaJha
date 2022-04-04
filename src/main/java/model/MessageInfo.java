package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to store all the data published for a topic.
 * @author nilimajha
 */
public class MessageInfo {

    private static final Logger logger = LogManager.getLogger(MessageInfo.class);
    private ArrayList<Long> flushedMessageOffset;
    private ArrayList<Long> inMemoryMessageOffset;
    private ArrayList<byte[]> inMemoryMessage;
    private AtomicLong lastOffSet = new AtomicLong(0); // updated after adding each message
    private FileOutputStream fileWriter = null;
    private FileInputStream fileReader = null;
    private String topicSegmentFileName;
    private boolean topicIsAvailable = true;
    private String topic;
    // lock for inMemory data store
    private final ReentrantReadWriteLock inMemoryDSLock = new ReentrantReadWriteLock();
    // lock for persistent storage and flushedMessageOffset ArrayList.
    private final ReentrantReadWriteLock persistentStorageAccessLock = new ReentrantReadWriteLock();
    private Timer timer;

    /**
     * Constructor to initialise class attributes.
     * @param topic topic to which this MessageInfo object belongs.
     */
    public MessageInfo(String topic) {
        this.flushedMessageOffset = new ArrayList<>();
        this.inMemoryMessageOffset = new ArrayList<>();
        this.inMemoryMessage = new ArrayList<>();
        this.topicSegmentFileName = topic + ".log";
        this.topic = topic;
        startTimer();
        fileWriterInitializer(this.topicSegmentFileName);
        fileReaderInitializer(this.topicSegmentFileName);
    }

    /**
     *
     */
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                flushIfNeeded();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, Constants.FLUSH_FREQUENCY);
    }

    /**
     * initialises the FileOutputStream named fileWriter
     * of the class and deletes the file if already exist.
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
            logger.error("\nFileNotFoundException while Initialising fileWriter for segmentFile. Error Message : " +e.getMessage());
        }
    }

    /**
     * initialises the FileInputStream named fileReader of the class.
     * @param fileName name of the segmentFile
     */
    private void fileReaderInitializer(String fileName) {
        try {
            fileReader = new FileInputStream(fileName);
        } catch (IOException e) {
            logger.error("\nIOException while Initialising fileReader for segmentFile. Error Message : " + e.getMessage());
        }
    }

    /**
     * writing inMemory data on the file using FileOutputStream named fileWriter.
     */
    public void flushOnFile() {
        // acquire write lock on the file and flushedMessageOffset ArrayList
        persistentStorageAccessLock.writeLock().lock();
        //flushing data on the file on file
        for (byte[] eachMessageByteArray : inMemoryMessage) {
            try {
                fileWriter.write(eachMessageByteArray);
            } catch (IOException e) {
                persistentStorageAccessLock.writeLock().unlock();
                logger.error("\nIOException while Writing on file. Error Message : " + e.getMessage());
            }
        }
        flushedMessageOffset.addAll(this.inMemoryMessageOffset);
        // clearing in-memory buffer of the published message.
        inMemoryMessageOffset.clear();
        inMemoryMessage.clear();

        logger.info("\n[FLUSH] Flushed In-Memory message of topic " + topic + " on the file " + topicSegmentFileName);
        // realising write lock on the file and flushedMessageOffset ArrayList
        persistentStorageAccessLock.writeLock().unlock();
    }

    /**
     * appending new message published by publisher to the inMemory buffer ArrayList.
     * and if after adding new message buffer ArrayList is full then
     * flushing those inMemory message to the file on the disk and
     * making it available for the consumer.
     * @param message message to be added
     * @return true
     */
    public boolean addNewMessage(byte[] message) {
        // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        inMemoryDSLock.writeLock().lock();
        inMemoryMessageOffset.add(lastOffSet.get());
        inMemoryMessage.add(message);
        lastOffSet.addAndGet(message.length);

        logger.info("\n[ADD] Added new message on Topic " + topic + ". [In-memory buffer size : " + inMemoryMessageOffset.size() + "]");

        if (inMemoryMessageOffset.size() == Constants.TOTAL_IN_MEMORY_MESSAGE_SIZE) {
            timer.cancel();
            flushOnFile();
            startTimer();
        }
        // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        inMemoryDSLock.writeLock().unlock();
        return true;
    }

    /**
     * reads 10 message from the offset given and returns it.
     * @param offSet offset from where data is to be extracted.
     * @return messageBatch
     */
    public ArrayList<byte[]> getMessage(long offSet) {
        persistentStorageAccessLock.readLock().lock();
        ArrayList<byte[]> messageBatch = null;
        int count = 0;
        AtomicLong currentOffset = new AtomicLong(offSet);
        // get the current offset index in the flushedMessageOffset ArrayList
        int index = flushedMessageOffset.indexOf(offSet);
        // offset is not available
        if (index == -1 && currentOffset.get() > flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {
            logger.info("\nOffset " + offSet + " is not yet available. Last Offset available is " + flushedMessageOffset.get(flushedMessageOffset.size() - 1));
            persistentStorageAccessLock.readLock().unlock();
            return messageBatch;
        } else {
            logger.info("\n Offset " + offSet + "is available.");
            messageBatch = new ArrayList<>();
        }
        while (count < Constants.MESSAGE_BATCH_SIZE && currentOffset.get() <= flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {

            // read one message at a time and append it on the messageBatch arrayList
            // making this block of code synchronised so that at a time only one thread can use FileInputStream named fileReader
            synchronized (this) {
                byte[] eachMessage = new byte[0];
                if (index == flushedMessageOffset.size() - 1) {
                    try {
                        fileReader.getChannel().position(currentOffset.get());
                        eachMessage = new byte[fileReader.available()];
                    } catch (IOException e) {
                        logger.error("\nIOException while setting the position of fileReader. Error Message : " +e.getMessage());
                    }
                } else {
                    int temp = (int) (flushedMessageOffset.get(index + 1) - currentOffset.get());
                    if (temp > 0){
                        eachMessage = new byte[temp];
                    }
                }

                try {
                    fileReader.getChannel().position(currentOffset.get());
                    fileReader.read(eachMessage);
                } catch (IOException e) {
                    logger.error("\nIOException while reading from segmentFile. Error Message : " + e.getMessage());
                }
                messageBatch.add(eachMessage);
                count++;
                if (index == flushedMessageOffset.size() - 1) {
                    currentOffset.set(flushedMessageOffset.get(index) + eachMessage.length);
                } else {
                    currentOffset.set(flushedMessageOffset.get(index + 1));
                }
                index++;
            }
        }
        persistentStorageAccessLock.readLock().unlock();
        return messageBatch;
    }

    /**
     * this method will keep running in a loop.
     * inside the loop it will first sleep for timeout amount of time,
     * then will wake up and flushes the in-memory data on to the file if needed.
     */
    private void flushIfNeeded() {
        timer.cancel();
        // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList
        inMemoryDSLock.writeLock().lock();
        logger.info("\nChecking if flushing is needed for topic '" + topic + "' [Total element in buffer : " + inMemoryMessageOffset.size() + "]");
        if (inMemoryMessageOffset.size() != 0) {
            flushOnFile();
        }
        // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        inMemoryDSLock.writeLock().unlock();
        startTimer();
    }

    /**
     * method will set the topicIsAvailable as false so that
     * the thread that is running and flushing the message onto the file if needed
     * after timeout can be stopped.
     */
    public void cancelTopic () {
        topicIsAvailable = false;
    }
}
