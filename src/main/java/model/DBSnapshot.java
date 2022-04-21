package model;

import java.util.concurrent.ConcurrentHashMap;

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
     *
     * @return
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     *
     * @return
     */
    public ConcurrentHashMap<String, TopicSnapshot> getTopicSnapshotMap() {
        return topicSnapshotMap;
    }

    /**
     * add new snapshot of a topic.
     * @param topic
     * @param topicSnapshot
     */
    public void addTopicSnapshot(String topic, TopicSnapshot topicSnapshot) {
        topicSnapshotMap.put(topic, topicSnapshot);
    }

}
