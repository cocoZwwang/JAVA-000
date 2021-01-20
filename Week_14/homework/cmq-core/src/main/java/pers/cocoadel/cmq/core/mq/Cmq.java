package pers.cocoadel.cmq.core.mq;


import pers.cocoadel.cmq.core.message.CmqMessage;

public interface Cmq  {
    boolean send(CmqMessage<?> message);

    CmqMessage<?> poll();

    CmqMessage<?> poll(long timeOutMills);

    CmqMessage<?> poll(String consumer);

    CmqMessage<?> poll(String consumer,long timeOutMills);

    void commit(String consumer);

    void setOffset(String consumer, int offset);

    void init(String topic, int capacity);
}
