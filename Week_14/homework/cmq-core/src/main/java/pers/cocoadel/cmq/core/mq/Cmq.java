package pers.cocoadel.cmq.core.mq;


import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.List;

public interface Cmq  {
    boolean send(CmqMessage<?> message);

    CmqMessage<?> poll();

    CmqMessage<?> poll(long timeOutMills);

    CmqMessage<?> poll(String consumer);

    CmqMessage<?> poll(String consumer,long timeOutMills);

    List<CmqMessage<?>> poll(String consumer, int count);

    List<CmqMessage<?>> poll(String consumer, int count,long timeOutMills);

    void commit(String consumer);

    void setOffset(String consumer, long offset);

    void init(String topic, int capacity);
}
