package model;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author nilimajha
 */
public class TopicSnapshot {
    private String topic;
    private AtomicLong offset;

    /**
     *
     * @param topic
     * @param offset
     */
    public TopicSnapshot(String topic, long offset) {
        this.topic = topic;
        this.offset = new AtomicLong(offset);
    }

    /**
     * getter for the attribute topic.
     * @return topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * getter for the attribute offset.
     * @return offset
     */
    public long getOffset() {
        return offset.get();
    }

    /**
     * setter for the attribute topic.
     * @param topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * setter for the attribute offset.
     * @param offset
     */
    public void setOffset(long offset) {
        this.offset.set(offset);
    }
}
