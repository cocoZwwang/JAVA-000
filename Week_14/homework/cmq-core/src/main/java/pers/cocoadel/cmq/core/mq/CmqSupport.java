package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.List;

public class CmqSupport implements Cmq {

    @Override
    public boolean send(CmqMessage<?> message) {
        return false;
    }

    @Override
    public CmqMessage<?> pollNow() {
        return null;
    }

    @Override
    public CmqMessage<?> pollNow(String consumer) {
        return null;
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        return null;
    }

    @Override
    public void commit(String consumer) {

    }

    @Override
    public void setOffset(String consumer, long offset) {

    }

    @Override
    public void init(String topic, int capacity) {

    }
}
