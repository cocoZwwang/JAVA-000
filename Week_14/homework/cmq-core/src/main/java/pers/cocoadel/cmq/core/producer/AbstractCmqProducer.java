package pers.cocoadel.cmq.core.producer;

import pers.cocoadel.cmq.core.broker.CmqBroker;

public abstract class AbstractCmqProducer implements CmqProducer {

    private CmqBroker cmqBroker;

    public CmqBroker getCmqBroker() {
        return cmqBroker;
    }

    public void setCmqBroker(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }
}
