package pers.cocoadel.cmq.core.broker;


import pers.cocoadel.cmq.core.consumer.AbstractCmqConsumer;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.AbstractTopicCmq;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.AbstractCmqProducer;
import pers.cocoadel.cmq.core.producer.CmqProducer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;


public class LocalMapCmqBroker extends AbstractCmqBroker {
    private final static int MAX_MQ_COUNT = 64;

    private final static int MAX_MQ_CAPACITY = 1000;

    private final Map<String, Cmq> map = new ConcurrentHashMap<>(MAX_MQ_COUNT);


    @Override
    public void createTopic(final String topic) {
        checkNotNull(cmqFactory);
        map.computeIfAbsent(topic, k -> {
            Cmq cmq = cmqFactory.createCmq();
            if (cmq != null) {
                cmq.init(topic, MAX_MQ_CAPACITY);
                ((AbstractTopicCmq) cmq).setTopic(topic);
            }
            return cmq;
        });
    }

    @Override
    public Cmq findMq(String topic) {
        return map.get(topic);
    }

    @Override
    public <T> CmqConsumer<T> createConsumer(Describe describe) {
        checkNotNull(cmqConsumerFactory);
        CmqConsumer<?> consumer = cmqConsumerFactory.createConsumer();
        if (consumer instanceof AbstractCmqConsumer) {
            AbstractCmqConsumer<?> abstractCmqConsumer = (AbstractCmqConsumer<?>) consumer;
            abstractCmqConsumer.setCmqBroker(this);
            abstractCmqConsumer.setDescribe(describe);
        }
        return (CmqConsumer<T>) consumer;
    }


    @Override
    public CmqProducer createProducer() {
        checkNotNull(cmqProducerFactory);
        CmqProducer cmqProducer = cmqProducerFactory.createProducer();
        if (cmqProducer instanceof AbstractCmqProducer) {
            ((AbstractCmqProducer) cmqProducer).setCmqBroker(this);
        }
        return cmqProducer;
    }
}
