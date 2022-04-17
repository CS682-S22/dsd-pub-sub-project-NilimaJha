package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that contains HashMap to store topic and message relate
 * @author nilimajha
 */
public class Data {
    private static final Logger logger = LogManager.getLogger(Data.class);
    private ConcurrentHashMap<String, MessageInfo> topicToMessageMap;
    private MembershipTable membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
    private BrokerInfo thisBrokerInfo;
    private static Data data = null;

    /**
     * Constructor to initialise topicToMessageMap.
     */
    private Data(BrokerInfo thisBrokerInfo) {
        this.topicToMessageMap = new ConcurrentHashMap<>();
        this.thisBrokerInfo = thisBrokerInfo;
    }

    /**
     * if the Data object is already initialised the reference of that object is passed.
     * if the object is not yet created then it will create an instance of it are return.
     * @return Data
     */
    public synchronized static Data getData(BrokerInfo thisBrokerInfo) {
        if (data == null) {
            data = new Data(thisBrokerInfo);
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
    public boolean addMessageToTopic(String typeOfMessage, String topic, byte[] messageByteArray, long offset) {
        MessageInfo messageInfo = getMessageInfoForTheTopic(topic);
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Adding new message.");
        return messageInfo.addMessage(typeOfMessage, offset, messageByteArray);
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

//    /**
//     * gets the offset of given messageId and the given topic from the topicToMessageMap
//     * @param topic
//     * @param offsetNumber
//     * @return message
//     */
//    public ArrayList<byte[]> getOffset(String topic, long offsetNumber) {
//        ArrayList<byte[]> messageBatch = null;
//        if (isTopicAvailable(topic)) {
//            messageBatch = topicToMessageMap.get(topic).getMessage(offsetNumber);
//        }
//        return messageBatch;
//    }

    /**
     * getter for topicToMessageMap
     * @return
     */
    public ConcurrentHashMap<String, MessageInfo> getTopicToMessageMap() {
        return topicToMessageMap;
    }

    /**
     * method returns the MessageInfo obj associated with the current topic.
     * if the topic is not available then it creates the topic and returns the new messageInfo obj.
     * @param topic
     * @return messageInfo obj mapped with the current topic.
     */
    public MessageInfo getMessageInfoForTheTopic(String topic) {
        if (!topicToMessageMap.containsKey(topic)) {
            logger.info("\nAdding New Topic '" + topic + "'");
            topicToMessageMap.putIfAbsent(topic, new MessageInfo(topic, thisBrokerInfo));
            logger.info("\n Topic : " + topic + " isUpToDate : " + topicToMessageMap.get(topic).getIsUpToDate());
        }
        return topicToMessageMap.get(topic);
    }

    /**
     * returns the list of all the topics available at the broker.
     * @return list of topics
     */
    public List<String> getTopicLists() {
        List<String> topics = new ArrayList<>();
        topics.addAll(topicToMessageMap.keySet());
        return topics;
    }

}
