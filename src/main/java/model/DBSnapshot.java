package model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Class is used to store the snapshot of the DB.
 * @author nilimajha
 */
public class DBSnapshot {
    private int memberId;
    private ConcurrentHashMap<String, TopicSnapshot> topicSnapshotMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     * @param memberId
     */
    public DBSnapshot(int memberId) {
        this.memberId = memberId;
    }

    /**
     * getter for the attribute memberId.
     * @return memberId
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     * getter for the attribute topicSnapshotMap.
     * @return topicSnapshotMap
     */
    public ConcurrentHashMap<String, TopicSnapshot> getTopicSnapshotMap() {
        return topicSnapshotMap;
    }

    /**
     * add new topic snapshot.
     * @param topic
     * @param topicSnapshot
     */
    public void addTopicSnapshot(String topic, TopicSnapshot topicSnapshot) {
        topicSnapshotMap.put(topic, topicSnapshot);
    }

}
