package pers.cocoadel.cmq.core.broker;

import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;

public class CmqBrokerSupport implements CmqBroker {

    @Override
    public void createTopic(String topic) {

    }

    @Override
    public Cmq findMq(String topic) {
        return null;
    }

    @Override
    public <T> CmqConsumer<T> createConsumer() {
        return null;
    }

    @Override
    public <T> CmqConsumer<T> createConsumer(String topic, String name) {
        return null;
    }

    @Override
    public CmqProducer createProducer() {
        return null;
    }
}
