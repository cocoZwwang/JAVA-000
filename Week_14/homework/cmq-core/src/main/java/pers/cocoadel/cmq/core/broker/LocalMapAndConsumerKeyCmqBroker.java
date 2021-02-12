package pers.cocoadel.cmq.core.broker;

import pers.cocoadel.cmq.core.consumer.AbstractCmqConsumer;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.AbstractCmqProducer;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.spi.ObjectFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalMapAndConsumerKeyCmqBroker implements CmqBroker {

    private final static int MAX_MQ_COUNT = 64;

    private final static int MAX_MQ_CAPACITY = 1000;

    private final Map<String, Cmq> map = new ConcurrentHashMap<>(MAX_MQ_COUNT);

    private final Map<Describe, CmqConsumer<?>> consumerMap = new ConcurrentHashMap<>();

    @Override
    public void createTopic(String topic) {
        map.computeIfAbsent(topic, k -> {
            Cmq cmq = ObjectFactory.createObject(Cmq.class);
            if (cmq != null) {
                cmq.init(topic, MAX_MQ_CAPACITY);
            }
            return cmq;
        });
    }

    @Override
    public Cmq findMq(String topic) {
        return map.get(topic);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> CmqConsumer<T> createConsumer(Describe describe) {
        checkNotNull(describe);
        String topic = describe.getTopic();
        if (!map.containsKey(topic)) {
            throw new IllegalArgumentException(String.format("the topic{%s} is not exist!", topic));
        }
        //如果相同名称的consumer已经存在则抛出异常
        synchronized (consumerMap) {
            if (consumerMap.containsKey(describe)) {
                throw new IllegalArgumentException(String.format("the consumer{%s} is exist!", describe.toString()));
            }
            CmqConsumer<?> consumer = ObjectFactory.createObject(CmqConsumer.class);
            if (consumer instanceof AbstractCmqConsumer) {
                ((AbstractCmqConsumer<?>) consumer).setCmqBroker(this);
            }
            consumerMap.put(describe, consumer);
        }
        return (CmqConsumer<T>) consumerMap.get(describe);
    }

    @Override
    public CmqProducer createProducer() {
        AbstractCmqProducer cmqProducer = ObjectFactory.createObject(AbstractCmqProducer.class);
        if (cmqProducer != null) {
            cmqProducer.setCmqBroker(this);
        }
        return cmqProducer;
    }


}
