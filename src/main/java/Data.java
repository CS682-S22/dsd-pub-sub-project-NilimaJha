import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * class that contains HashMap to store topic and message relate
 * @author nilimajha
 */
public class Data {
    private HashMap<String, ArrayList<byte[]>> topicToMessageMap;
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
        this.lock.readLock().lock();          // acquire read lock on topicToMessageMap
        if (this.topicToMessageMap.containsKey(topic)) {
            this.lock.readLock().unlock();    // realising read lock on topicToMessageMap
            return true;
        }
        this.lock.readLock().unlock();        // realising read lock on topicToMessageMap
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
        this.lock.writeLock().lock();         // acquiring write lock on topicToMessageMap
        if (!this.topicToMessageMap.containsKey(topic)) {
            this.topicToMessageMap.put(topic, new ArrayList<byte[]>());
        }
        this.topicToMessageMap.get(topic).add(messageByteArray);
        this.lock.writeLock().unlock();      // realising write lock on topicToMessageMap
        return true;
    }

    /**
     * gets the message of given messageId and the given topic from the topicToMessageMap
     * @param topic
     * @param messageId
     * @return message
     */
    public byte[] getMessage(String topic, int messageId) {
        // might need to make this synchronised block.
        this.lock.readLock().lock();        // acquire read lock on topicToMessageMap
        byte[] message = null;
        if (this.topicToMessageMap.get(topic).size() >= messageId) {
            message = this.topicToMessageMap.get(topic).get(messageId);
        }
        this.lock.readLock().unlock();     // realising read lock on topicToMessageMap
        return message;
    }
}
