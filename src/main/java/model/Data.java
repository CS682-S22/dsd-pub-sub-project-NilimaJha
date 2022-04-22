package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that contains HashMap to store topic and message relate
 * the brokerInfo at which this Data is available.
 * This is a Singleton class.
 * @author nilimajha
 */
public class Data {
    private static final Logger logger = LogManager.getLogger(Data.class);
    private ConcurrentHashMap<String, MessageInfo> topicToMessageMap;
    private MembershipTable membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
    private BrokerInfo thisBrokerInfo;
    private String loadBalancerIP;
    private int loadBalancerPort;
    private static Data data = null;

    /**
     * Constructor to initialise the class attribute.
     */
    private Data(BrokerInfo thisBrokerInfo, String loadBalancerIP, int loadBalancerPort) {
        this.topicToMessageMap = new ConcurrentHashMap<>();
        this.thisBrokerInfo = thisBrokerInfo;
        this.loadBalancerIP = loadBalancerIP;
        this.loadBalancerPort = loadBalancerPort;
    }

    /**
     * if the Data object is already initialised the reference of that object is passed.
     * if the object is not yet created then it will create an instance of it are return.
     * @return Data
     */
    public synchronized static Data getData(BrokerInfo thisBrokerInfo, String loadBalancerIP, int loadBalancerPort) {
        if (data == null) {
            data = new Data(thisBrokerInfo, loadBalancerIP, loadBalancerPort);
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
    public ArrayList<byte[]> getMessage(String topic, long offsetNumber, String requester) {
        ArrayList<byte[]> messageBatch = null;
        if (isTopicAvailable(topic)) {
            if (!requester.equals(Constants.BROKER)) {
//                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] pull request from Consumer from offset : " + offsetNumber);
                messageBatch = topicToMessageMap.get(topic).getMessage(offsetNumber);
            } else {
//                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] pull request from Broker from offset : " + offsetNumber);
                messageBatch = topicToMessageMap.get(topic).getMessage(offsetNumber);
                if (messageBatch == null || messageBatch.size() == 0) {
//                    logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] persistant storage message is sent now checking inmemory for broker MessageIsSent ");
                    messageBatch = topicToMessageMap.get(topic).getMessageForBroker(offsetNumber);
                }
            }
        }
        return messageBatch;
    }

    /**
     * getter for the attribute topicToMessageMap.
     * @return topicToMessageMap.
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
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Adding New Topic '" + topic + "'");
            topicToMessageMap.putIfAbsent(topic, new MessageInfo(topic, thisBrokerInfo, loadBalancerIP, loadBalancerPort));
//            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Topic : " + topic + " isUpToDate : " + topicToMessageMap.get(topic).getIsUpToDate());
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

    /**
     * getter for the attribute loadBalancerIp.
     * @return loadBalancerIP
     */
    public String getLoadBalancerIP() {
        return loadBalancerIP;
    }

    /**
     * getter for attribute loadBalancerPort
     * @return
     */
    public int getLoadBalancerPort() {
        return loadBalancerPort;
    }

    /**
     * current snapshot of the DB.
     * @return
     */
    public DBSnapshot getSnapshot() {
        DBSnapshot dbSnapshot = new DBSnapshot(thisBrokerInfo.getBrokerId());
        for (Map.Entry<String, MessageInfo> eachTopicInfo : topicToMessageMap.entrySet()) {
            TopicSnapshot topicSnapshot = new TopicSnapshot(eachTopicInfo.getKey(), eachTopicInfo.getValue().getLastOffSet());
            dbSnapshot.addTopicSnapshot(eachTopicInfo.getKey(), topicSnapshot);
        }
        return dbSnapshot;
    }

    /**
     * method sets the value of attribute upToDate.
     * @param status
     * @return true
     */
    public boolean setUpToDate(boolean status) {
        for (Map.Entry<String, MessageInfo> eachTopicMessageSet : topicToMessageMap.entrySet()) {
            eachTopicMessageSet.getValue().setUpToDate(status);
        }
        return true;
    }
}
