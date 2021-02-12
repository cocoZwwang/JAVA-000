package pers.cocoadel.cmq.core.consumer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.Describe;

public abstract class AbstractCmqConsumer<T> implements CmqConsumer<T> {

    private CmqBroker cmqBroker;

    private Describe describe;


    public Describe getDescribe() {
        return describe;
    }


    public void setDescribe(Describe describe) {
        this.describe = describe;
    }

    public CmqBroker getCmqBroker() {
        return cmqBroker;
    }

    public void setCmqBroker(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }
}
