package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * class that contains HashMap to store topic and message relate
 * @author nilimajha
 */
public class Data {
    private HashMap<String, MessageInfo> topicToMessageMap;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructor to initialise topicToMessageMap.
     */
    public Data() {
        this.topicToMessageMap = new HashMap<>();
    }

    /**
     * checks if the topic is available in the topicToMessageMap.
     * @param topic
     * @return true/false
     */
    public boolean isTopicAvailable(String topic) {
        lock.readLock().lock();          // acquire read lock on topicToMessageMap
        if (topicToMessageMap.containsKey(topic)) {
            lock.readLock().unlock();    // realising read lock on topicToMessageMap
            return true;
        }
        lock.readLock().unlock();        // realising read lock on topicToMessageMap
        return false;
    }

    /**
     * checks if topic is available in the topicToMessageMap,
     * if it is available adds the new message to the already existing message list and returns true
     * else creates one and then adds the message to it and returns true.
     * @param topic
     * @param messageByteArray
     * @return true
     */
    public boolean addMessage(String topic, byte[] messageByteArray) {
        lock.writeLock().lock();         // acquiring write lock on topicToMessageMap
        if (!topicToMessageMap.containsKey(topic)) {
            System.out.printf("[Adding Topic '%s']\n", topic);
            topicToMessageMap.put(topic, new MessageInfo(topic));
        }
        topicToMessageMap.get(topic).addNewMessage(messageByteArray);
        lock.writeLock().unlock();      // realising write lock on topicToMessageMap
        return true;
    }

    /**
     * gets the message of given messageId and the given topic from the topicToMessageMap
     * @param topic
     * @param offsetNumber
     * @return message
     */
    public ArrayList<byte[]> getMessage(String topic, int offsetNumber) {
        lock.readLock().lock();        // acquire read lock on topicToMessageMap
        ArrayList<byte[]> messageBatch = null;
        if (isTopicAvailable(topic)) {
            messageBatch = topicToMessageMap.get(topic).getMessage(offsetNumber);
        }
        lock.readLock().unlock();     // realising read lock on topicToMessageMap
        return messageBatch;
    }

    /**
     * getter for topicToMessageMap
     * @return
     */
    public HashMap<String, MessageInfo> getTopicToMessageMap() {
        return topicToMessageMap;
    }

}
