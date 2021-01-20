package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

public class CmqSupport implements Cmq {
    @Override
    public boolean send(CmqMessage<?> message) {
        return false;
    }

    @Override
    public CmqMessage<?> poll() {
        return null;
    }

    @Override
    public CmqMessage<?> poll(long timeOutMills) {
        return null;
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        return null;
    }

    @Override
    public CmqMessage<?> poll(String consumer, long timeOutMills) {
        return null;
    }

    @Override
    public void commit(String consumer) {

    }

    @Override
    public void setOffset(String consumer, int offset) {

    }

    @Override
    public void init(String topic, int capacity) {

    }
}
