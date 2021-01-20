package pers.cocoadel.cmq.core.consumer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.mq.Cmq;

public class GenericLocalCmqConsumer<T> extends CmqConsumerSupport<T> {

    private Cmq cmq;

    private CmqBroker broker;

    public GenericLocalCmqConsumer() {

    }

    public GenericLocalCmqConsumer(CmqBroker broker) {
        this.broker = broker;
    }

    @Override
    public void setCmqBroker(CmqBroker cmqBroker) {
        this.broker = cmqBroker;
    }

    @Override
    public void subscribe(String topic) {
        cmq = broker.findMq(topic);
    }

    @Override
    public CmqMessage<T> poll() {
        return (CmqMessage<T>) cmq.poll();
    }

    @Override
    public CmqMessage<T> poll(long timeOutMills) {
        return (CmqMessage<T>) cmq.poll(timeOutMills);
    }
}
