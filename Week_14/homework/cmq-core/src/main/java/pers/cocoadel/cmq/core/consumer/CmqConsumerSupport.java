package pers.cocoadel.cmq.core.consumer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;

public class CmqConsumerSupport<T> implements CmqConsumer<T> {

    @Override
    public void subscribe(String topic) {

    }

    @Override
    public CmqMessage<T> poll() {
        return null;
    }

    @Override
    public CmqMessage<T> poll(long timeOutMills) {
        return null;
    }

    @Override
    public void commit() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setCmqBroker(CmqBroker cmqBroker){

    }

    @Override
    public void setName(String name) {

    }
}
