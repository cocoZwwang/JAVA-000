package pers.cocoadel.cmq.core.broker;


import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;

public interface CmqBroker {
    void createTopic(String topic);

    Cmq findMq(String topic);

    <T> CmqConsumer<T> createConsumer();

    <T> CmqConsumer<T> createConsumer(String topic, String name);

    CmqProducer createProducer();
}
