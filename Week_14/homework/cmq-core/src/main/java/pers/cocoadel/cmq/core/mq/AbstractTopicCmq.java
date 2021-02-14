package pers.cocoadel.cmq.core.mq;

public abstract class AbstractTopicCmq implements Cmq {
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
