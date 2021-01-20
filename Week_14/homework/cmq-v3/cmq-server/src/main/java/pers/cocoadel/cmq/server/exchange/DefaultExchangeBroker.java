package pers.cocoadel.cmq.server.exchange;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.spi.ObjectFactory;

/**
 * 连接 远程通信 和 本地 broker
 */
public class DefaultExchangeBroker implements ExchangeBroker {

    private final CmqBroker cmqBroker;

    public DefaultExchangeBroker() {
        this.cmqBroker = ObjectFactory.createObject(CmqBroker.class);
    }

    @Override
    public void createTopic(String topic) {
        cmqBroker.createTopic(topic);
    }

    @Override
    public Cmq findMq(String topic) {
        return cmqBroker.findMq(topic);
    }

    @Override
    public <T> CmqConsumer<T> createConsumer() {
        return cmqBroker.createConsumer();
    }

    @Override
    public <T> CmqConsumer<T> createConsumer(String topic, String name) {
        return cmqBroker.createConsumer(topic,name);
    }

    @Override
    public CmqProducer createProducer() {
        return cmqBroker.createProducer();
    }
}
