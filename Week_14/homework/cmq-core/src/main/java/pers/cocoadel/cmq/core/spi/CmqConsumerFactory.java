package pers.cocoadel.cmq.core.spi;

import pers.cocoadel.cmq.core.consumer.CmqConsumer;

public class CmqConsumerFactory {
    private final static CmqConsumerFactory INSTANCE = new CmqConsumerFactory();

    public static CmqConsumerFactory getInstance() {
        return INSTANCE;
    }

    private CmqConsumerFactory() {

    }

    public <T> CmqConsumer<T> createConsumer() {
        return ObjectFactory.createObject(CmqConsumer.class);
    }
}
