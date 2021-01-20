package pers.cocoadel.cmq.core.consumer;


import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;

public interface CmqConsumer<T> {

    void subscribe(String topic);

    CmqMessage<T> poll();

    CmqMessage<T> poll(long timeOutMills);

    void commit();

    String getName();

    void setCmqBroker(CmqBroker cmqBroker);

    void setName(String name);
}
