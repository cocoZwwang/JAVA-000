package pers.cocoadel.cmq.core.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.spi.ObjectFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalMapAndConsumerKeyCmqBroker extends CmqBrokerSupport {

    private final static int MAX_MQ_COUNT = 64;

    private final static int MAX_MQ_CAPACITY = 1000;

    private final Map<String, Cmq> map = new ConcurrentHashMap<>(MAX_MQ_COUNT);

    private final Map<ConsumerKey, CmqConsumer<?>> consumerMap = new ConcurrentHashMap<>();

    @Override
    public void createTopic(String topic) {
        map.computeIfAbsent(topic,k ->{
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

    @Override
    public <T> CmqConsumer<T> createConsumer(String topic, String name) {
        if (!map.containsKey(topic)) {
            throw new IllegalArgumentException(String.format("the topic{%s} is not exist!", topic));
        }
        //如果相同名称的consumer已经存在则抛出异常
        ConsumerKey key = new ConsumerKey(name);
        synchronized (consumerMap){
            if (consumerMap.containsKey(key)) {
                throw new IllegalArgumentException(String.format("the consumer{%s} is exist!",key));
            }
            CmqConsumer<?> consumer = ObjectFactory.createObject(CmqConsumer.class);
            if (consumer != null) {
                consumer.setCmqBroker(this);
                consumer.setName(name);
            }
            consumerMap.put(key, consumer);
        }
        return (CmqConsumer<T>) consumerMap.get(key);
    }

    @Override
    public CmqProducer createProducer() {
        CmqProducer cmqProducer = ObjectFactory.createObject(CmqProducer.class);
        if (cmqProducer != null) {
            cmqProducer.setCmqBroker(this);
        }
        return cmqProducer;
    }

    @Data
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    private static class ConsumerKey {
        private String name;
    }

}
