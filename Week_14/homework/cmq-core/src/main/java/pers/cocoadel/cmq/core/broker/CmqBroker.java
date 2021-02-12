package pers.cocoadel.cmq.core.broker;


import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;

public interface CmqBroker {
    void createTopic(String topic);

    Cmq findMq(String topic);

    <T> CmqConsumer<T> createConsumer(Describe describe);

    CmqProducer createProducer();
}
