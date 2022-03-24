package model;

import java.io.*;
import java.util.ArrayList;
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
    private final ReentrantReadWriteLock inMemoryDSLock = new ReentrantReadWriteLock(); // lock for inMemory data store
    private final ReentrantReadWriteLock persistentStorageAccessLock = new ReentrantReadWriteLock(); // lock for persistent storage and flushedMessageOffset ArrayList.
    Thread thread = new Thread() {
        @Override
        public void run() {
            flushIfNeeded();
        }
    };

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
        thread.start();
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
        inMemoryDSLock.writeLock().lock();
        inMemoryMessageOffset.add(this.lastOffSet);
        inMemoryMessage.add(message);
        lastOffSet += message.length;
        System.out.printf("\n[Thread Id : %s] [ADD] Added new message on Topic %s. [In-memory buffer size : %d]\n", Thread.currentThread().getId(), topic, inMemoryMessageOffset.size());
        if (inMemoryMessageOffset.size() == Constants.TOTAL_IN_MEMORY_MESSAGE_SIZE) {
            flushOnFile();
        }
        // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        inMemoryDSLock.writeLock().unlock();
        return true;
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
                e.printStackTrace();
            }
        }
        flushedMessageOffset.addAll(this.inMemoryMessageOffset);
        // clearing in-memory buffer of the published message.
        inMemoryMessageOffset.clear();
        inMemoryMessage.clear();
        System.out.printf("\n[Thread Id : %s] [FLUSH] Flushed In-Memory message of topic %s on the file %s.\n", Thread.currentThread().getId(), topic, topicSegmentFileName);
        // realising write lock on the file and flushedMessageOffset ArrayList
        persistentStorageAccessLock.writeLock().unlock();
    }

    /**
     * reads 10 message from the offset given and returns it.
     * @param offSet offset from where data is to be extracted.
     * @return messageBatch
     */
    public ArrayList<byte[]> getMessage(int offSet) {
        persistentStorageAccessLock.readLock().lock();
        ArrayList<byte[]> messageBatch = null;
        int count = 0;
        int currentOffset = offSet;
        // get the current offset index in the flushedMessageOffset ArrayList
        int index = flushedMessageOffset.indexOf(offSet);
        // offset is not available
        if (index == -1 && currentOffset > flushedMessageOffset.get(flushedMessageOffset.size() - 1)) {
            System.out.printf("\n[Thread Id : %s] Offset %d is not yet available. Last Offset available is %d.\n", Thread.currentThread().getId(), offSet, flushedMessageOffset.get(flushedMessageOffset.size() - 1));
            persistentStorageAccessLock.readLock().unlock();
            return messageBatch;
        } else {
            System.out.printf("\n[Thread Id : %s] Offset %d is available.\n", Thread.currentThread().getId(), offSet);
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
                    int temp = flushedMessageOffset.get(index + 1) - currentOffset;
                    if (temp > 0){
                        eachMessage = new byte[temp];
                    }
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
        persistentStorageAccessLock.readLock().unlock();
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList
            inMemoryDSLock.writeLock().lock();
            System.out.printf("\n[Thread Id : %s] Checking if flushing is needed for topic %s [Total element in buffer : %d] \n", Thread.currentThread().getId(), topic, inMemoryMessageOffset.size());
            if (inMemoryMessageOffset.size() != 0) {
                flushOnFile();
            }
            // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
            inMemoryDSLock.writeLock().unlock();
        }
    }

    /**
     * method will set the topicIsAvailable as false so that
     * the thread that is running and flushing the message onto the file if needed
     * after timeout can be stopped.
     */
    public void cancelTopic () {
        topicIsAvailable = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
