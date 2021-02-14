package pers.cocoadel.cmq.core.spi;

import pers.cocoadel.cmq.core.producer.CmqProducer;

public class CmqProducerFactory {
    private final static CmqProducerFactory INSTANCE = new CmqProducerFactory();

    public static CmqProducerFactory getInstance() {
        return INSTANCE;
    }


    private CmqProducerFactory() {

    }

    public CmqProducer createProducer() {
        return ObjectFactory.createObject(CmqProducer.class);
    }
}
