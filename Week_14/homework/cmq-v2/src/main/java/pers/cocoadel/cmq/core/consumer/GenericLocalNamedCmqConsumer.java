package pers.cocoadel.cmq.core.consumer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.mq.Cmq;

public class GenericLocalNamedCmqConsumer<T> extends CmqConsumerSupport<T> {
    private CmqBroker broker;

    private Cmq cmq;

    private String name;

    public GenericLocalNamedCmqConsumer(final CmqBroker broker,final String name) {
        this.broker = broker;
        this.name = name;
    }

    public GenericLocalNamedCmqConsumer() {

    }

    @Override
    public void subscribe(String topic) {
        this.cmq = broker.findMq(topic);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setCmqBroker(CmqBroker cmqBroker) {
        this.broker = cmqBroker;
    }

    @Override
    public CmqMessage<T> poll() {
        return cmq == null ? null : (CmqMessage<T>) cmq.poll(getName());
    }

    @Override
    public CmqMessage<T> poll(long timeOutMills) {
        return cmq == null ? null : (CmqMessage<T>) cmq.poll(getName(),timeOutMills);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void commit() {
        if (cmq != null) {
            cmq.commit(getName());
        }
    }
}
