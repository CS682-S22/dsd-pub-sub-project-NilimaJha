package model;

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
    private ArrayList<Integer> flushedMessageOffset;
    private ArrayList<Integer> inMemoryMessageOffset;
    private ArrayList<byte[]> inMemoryMessage;
    private int lastOffSet = 0; // updated after adding each message
    private FileOutputStream fileWriter = null;
    private FileInputStream fileReader = null;
    private String topicSegmentFileName;
    private boolean topicIsAvailable = true;
    private String topic;
    private final ReentrantReadWriteLock lock1 = new ReentrantReadWriteLock(); // lock for inMemory data store
    private final ReentrantReadWriteLock lock2 = new ReentrantReadWriteLock(); // lock for persistent storage and flushedMessageOffset ArrayList.
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

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
        threadPool.execute(this::flushIfNeeded);
        fileWriterInitializer(this.topicSegmentFileName);
        fileReaderInitializer(this.topicSegmentFileName);
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
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
        lock1.writeLock().lock();
        inMemoryMessageOffset.add(this.lastOffSet);
        inMemoryMessage.add(message);
        lastOffSet += message.length;
        System.out.println("[Thread Id : " + Thread.currentThread().getId() + "] added new message. [In-memory buffer size " + inMemoryMessageOffset.size() + "]");
        if (inMemoryMessageOffset.size() == Constants.TOTAL_IN_MEMORY_MESSAGE_SIZE) {
            System.out.println("[Thread Id : " + Thread.currentThread().getId() + "] Flushing needed.");
            flushOnFile();
            System.out.println("[Thread Id : " + Thread.currentThread().getId() + "] Flushing is done.");
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
        System.out.printf("\n[Thread Id : %s] [Total element in buffer after flushing : %d]\n", Thread.currentThread().getId(), this.inMemoryMessageOffset.size());
        // realising write lock on the file and flushedMessageOffset ArrayList
        lock2.writeLock().unlock();
    }

    /**
     * reads 10 message from the offset given and returns it.
     * @param offSet offset from where data is to be extracted.
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
            System.out.printf("\n[Thread Id : %s] [offset number %d is not available. String last Offset number available %d]\n", Thread.currentThread().getId(), offSet, flushedMessageOffset.get(flushedMessageOffset.size() - 1));
            lock2.readLock().unlock();
            return messageBatch;
        } else if (index == -1) {
            System.out.printf("\n[Thread Id : %s] [offset number %d is not available. String last Offset number available %d]\n", Thread.currentThread().getId(), offSet, flushedMessageOffset.get(flushedMessageOffset.size() - 1));
            lock2.readLock().unlock();
            return messageBatch;
        } else {
            System.out.printf("\n[Thread Id : %s] [offset number is available.]\n", Thread.currentThread().getId());
            messageBatch = new ArrayList<>();
        }
        while (count < Constants.MESSAGE_BATCH_SIZE && currentOffset <= flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {
            // read one message at a time and append it on the messageBatch arrayList
            synchronized (this) { // making this block of code synchronised so that at a time only one thread can use FileInputStream named fileReader
                byte[] eachMessage = new byte[0];
                if (index == flushedMessageOffset.size() - 1) {
                    try {
                        fileReader.getChannel().position(currentOffset);
                        eachMessage = new byte[fileReader.available()];
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    eachMessage = new byte[flushedMessageOffset.get(index + 1) - currentOffset];
                }

                try {
                    fileReader.getChannel().position(currentOffset);
                    fileReader.read(eachMessage);
                } catch (IOException e) {
                    e.printStackTrace();
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
                System.out.printf("\n[Thread Id : %s] [sleeping for 6000 milli sec] \n", Thread.currentThread().getId());
                Thread.sleep(Constants.FLUSH_FREQUENCY);
                System.out.printf("\n[Thread Id : %s] [Awake after 6000 ms] \n", Thread.currentThread().getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList
            lock1.writeLock().lock();
            System.out.printf("\nChecking if flushing is needed[Total element in buffer : %d] \n", inMemoryMessageOffset.size());
            if (inMemoryMessageOffset.size() != 0) {
                System.out.printf("\nFlushing on File. [Total element in buffer : %d] \n", inMemoryMessageOffset.size());
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
