package pers.cocoadel.cmq.core.spi;

import pers.cocoadel.cmq.core.mq.Cmq;

public class CmqFactory {
    private final static CmqFactory INSTANCE = new CmqFactory();

    public static CmqFactory getInstance() {
        return INSTANCE;
    }

    private CmqFactory() {

    }

    public Cmq createCmq() {
        return ObjectFactory.createObject(Cmq.class);
    }

}
