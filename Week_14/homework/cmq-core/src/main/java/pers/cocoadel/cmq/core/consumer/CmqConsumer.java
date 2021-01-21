package pers.cocoadel.cmq.core.consumer;


import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;

public interface CmqConsumer<T> {

    boolean subscribe(String topic);

    CmqMessage<T> poll();

    CmqMessage<T> poll(long timeOutMills);

    boolean commit();

    String getName();

    void setCmqBroker(CmqBroker cmqBroker);

    void setName(String name);
}
