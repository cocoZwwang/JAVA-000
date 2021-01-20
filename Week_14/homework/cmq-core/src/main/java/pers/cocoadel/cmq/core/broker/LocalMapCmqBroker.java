package pers.cocoadel.cmq.core.broker;


import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.mq.Cmq;
import pers.cocoadel.cmq.core.mq.LocalBlockingQueueCmq;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.spi.ObjectFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LocalMapCmqBroker extends CmqBrokerSupport {
    private final static int MAX_MQ_COUNT = 64;

    private final static int MAX_MQ_CAPACITY = 1000;

    private final Map<String, Cmq> map = new ConcurrentHashMap<>(MAX_MQ_COUNT);

    @Override
    public void createTopic(final String topic) {
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
    public <T> CmqConsumer<T> createConsumer() {
        CmqConsumer<?> consumer = ObjectFactory.createObject(CmqConsumer.class);
        if (consumer != null) {
            consumer.setCmqBroker(this);
        }
        return (CmqConsumer<T>) consumer;
    }

    @Override
    public CmqProducer createProducer() {
        CmqProducer cmqProducer = ObjectFactory.createObject(CmqProducer.class);
        if (cmqProducer != null) {
            cmqProducer.setCmqBroker(this);
        }
        return cmqProducer;
    }

}
