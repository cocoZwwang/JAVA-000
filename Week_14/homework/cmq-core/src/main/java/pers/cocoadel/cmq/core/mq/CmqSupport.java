package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.List;

public class CmqSupport implements Cmq {
    @Override
    public boolean send(CmqMessage<?> message) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public CmqMessage<?> poll() {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public CmqMessage<?> poll(long timeOutMills) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public CmqMessage<?> poll(String consumer, long timeOutMills) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public List<CmqMessage<?>> poll(String consumer, int count) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public List<CmqMessage<?>> poll(String consumer, int count,long timeOutMills) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public void commit(String consumer) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public void setOffset(String consumer, long offset) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }

    @Override
    public void init(String topic, int capacity) {
        throw new UnsupportedOperationException(this.getClass().getName() + " did not implements the method!");
    }
}
