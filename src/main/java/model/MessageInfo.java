package model;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageInfo {
    private ArrayList<Integer> flushedMessageOffset;
    private ArrayList<Integer> inMemoryMessageOffset;
    private ArrayList<byte[]> inMemoryMessage;
    private int lastOffSet = 0; // updated after adding each message
    private FileOutputStream fileWriter = null;
    private FileInputStream fileReader = null;
    private String topicFileName;
    final ReentrantReadWriteLock lock1 = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock lock2 = new ReentrantReadWriteLock();


    /**
     * Constructor to initialise class attributes.
     * @param topic
     */
    public MessageInfo(String topic) {
        this.flushedMessageOffset = new ArrayList<>();
        this.inMemoryMessageOffset = new ArrayList<>();
        this.inMemoryMessage = new ArrayList<>();
        this.topicFileName = topic + ".log";
        fileWriterInitializer(this.topicFileName);
        fileReaderInitializer(this.topicFileName);
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
            this.fileReader = new FileInputStream(fileName);
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
        // acquire write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList
        this.lock1.writeLock().lock();
        this.inMemoryMessageOffset.add(this.lastOffSet);
        this.inMemoryMessage.add(message);
        this.lastOffSet += message.length;
        if (this.inMemoryMessageOffset.size() == Constants.TOTAL_IN_MEMORY_MESSAGE_SIZE) {
            flushOnFile();
            // clearing in-memory buffer of the published message.
            this.inMemoryMessageOffset.clear();
            this.inMemoryMessage.clear();
        }

        // release write lock on inMemoryOffset Arraylist and inMemoryMessage ArrayList.
        this.lock1.writeLock().unlock();
        return true;
    }

    /**
     * writing inMemory data on the file using FileOutputStream named fileWriter.
     */
    public void flushOnFile() {
        // acquire write lock on the file and flushedMessageOffset ArrayList
        this.lock2.writeLock().lock();
        //flushing data on the file on file
        for (byte[] eachMessageByteArray : this.inMemoryMessage) {
            try {
                this.fileWriter.write(eachMessageByteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.flushedMessageOffset.addAll(this.inMemoryMessageOffset);
        // realising write lock on the file and flushedMessageOffset ArrayList
        this.lock2.writeLock().unlock();
    }

    /**
     * reads 10 message from the offset given and returns it.
     * @param offSet
     * @return messageBatch
     */
    public ArrayList<byte[]> getMessage(int offSet) {
        this.lock2.readLock().lock();
        ArrayList<byte[]> messageBatch = null;
        int count = 0;
        int currentOffset = offSet;
        // get the current offset index in the flushedMessageOffset ArrayList
        int index = this.flushedMessageOffset.indexOf(offSet);
        // offset is not available
        if (index == -1 && currentOffset > this.flushedMessageOffset.get(this.flushedMessageOffset.size() - 1)) {
            System.out.printf("\n[Thread Id : %s] [offset number %d is not available. String last Offset number available %d]\n", Thread.currentThread().getId(), offSet, this.flushedMessageOffset.get(this.flushedMessageOffset.size() - 1));
            this.lock2.readLock().unlock();
            return messageBatch;
        } else if (index == -1) {
            System.out.printf("\n[Thread Id : %s] [offset number %d is not available. String last Offset number available %d]\n", Thread.currentThread().getId(), offSet, this.flushedMessageOffset.get(this.flushedMessageOffset.size() - 1));
            this.lock2.readLock().unlock();
            return messageBatch;
        } else {
            System.out.printf("\n[Thread Id : %s] [offset number is available.]\n", Thread.currentThread().getId());
            messageBatch = new ArrayList<>();
        }
        while (count < Constants.MESSAGE_BATCH_SIZE && currentOffset <= this.flushedMessageOffset.get(this.flushedMessageOffset.size() - 1)) {
            // read one message at a time and append it on the messageBatch arrayList
            synchronized (this) { // making this block of code synchronised so that at a time only one thread can use FileInputStream named fileReader
                byte[] eachMessage = new byte[0];
                if (index == this.flushedMessageOffset.size() - 1) {
                    try {
                        this.fileReader.getChannel().position(currentOffset);
                        eachMessage = new byte[this.fileReader.available()];
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    eachMessage = new byte[this.flushedMessageOffset.get(index + 1) - currentOffset];
                }

                try {
                    this.fileReader.getChannel().position(currentOffset);
                    fileReader.read(eachMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageBatch.add(eachMessage);
                count++;
                if (index == this.flushedMessageOffset.size() - 1) {
                    currentOffset = this.flushedMessageOffset.get(index) + eachMessage.length;
                } else {
                    currentOffset = this.flushedMessageOffset.get(index + 1);
                }
                index++;
            }
        }
        this.lock2.readLock().unlock();
        return messageBatch;
    }
}
