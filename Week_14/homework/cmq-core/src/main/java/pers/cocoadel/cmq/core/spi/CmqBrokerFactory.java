package pers.cocoadel.cmq.core.spi;

import pers.cocoadel.cmq.core.broker.CmqBroker;

public class CmqBrokerFactory {

    private static final CmqBrokerFactory INSTANCE = new CmqBrokerFactory();

    public static CmqBrokerFactory getInstance() {
        return INSTANCE;
    }

    private CmqBrokerFactory() {

    }

    public CmqBroker createCmqBroker() {
        return ObjectFactory.createObject(CmqBroker.class);
    }
}
