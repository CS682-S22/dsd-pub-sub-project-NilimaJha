package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that contains HashMap to store topic and message relate
 * @author nilimajha
 */
public class Data {
    private static final Logger logger = LogManager.getLogger(Data.class);
    private ConcurrentHashMap<String, MessageInfo> topicToMessageMap;
    private static Data data = null;

    /**
     * Constructor to initialise topicToMessageMap.
     */
    private Data() {
        this.topicToMessageMap = new ConcurrentHashMap<>();
    }

    /**
     * if the Data object is already initialised the reference of that object is passed.
     * if the object is not yet created then it will create an instance of it are return.
     * @return Data
     */
    public synchronized static Data getData() {
        if (data == null) {
            data = new Data();
        }
        return data;
    }

    /**
     * checks if the topic is available in the topicToMessageMap.
     * @param topic
     * @return true/false
     */
    public boolean isTopicAvailable(String topic) {
        return topicToMessageMap.containsKey(topic);
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
        MessageInfo messageInfo = getMessageInfoForTheTopic(topic);
        return messageInfo.addNewMessage(messageByteArray);
    }

    /**
     * gets the message of given messageId and the given topic from the topicToMessageMap
     * @param topic
     * @param offsetNumber
     * @return message
     */
    public ArrayList<byte[]> getMessage(String topic, long offsetNumber) {
        ArrayList<byte[]> messageBatch = null;
        if (isTopicAvailable(topic)) {
            messageBatch = topicToMessageMap.get(topic).getMessage(offsetNumber);
        }
        return messageBatch;
    }

    /**
     * getter for topicToMessageMap
     * @return
     */
    public ConcurrentHashMap<String, MessageInfo> getTopicToMessageMap() {
        return topicToMessageMap;
    }

    /**
     *
     * @param topic
     * @return
     */
    public MessageInfo getMessageInfoForTheTopic(String topic) {
        if (!topicToMessageMap.containsKey(topic)) {
            logger.info("\nAdding New Topic '" + topic + "'");
            topicToMessageMap.putIfAbsent(topic, new MessageInfo(topic));
        }
        return topicToMessageMap.get(topic);
    }

}
