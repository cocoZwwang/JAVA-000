package pers.cocoadel.cmq.core.consumer;

import pers.cocoadel.cmq.core.message.CmqMessage;
import java.util.List;

public class CmqConsumerSupport<T> implements CmqConsumer<T> {

    @Override
    public boolean subscribe(String topic) {
        return false;
    }

    @Override
    public CmqMessage<T> pollNow() {
        return null;
    }

    @Override
    public List<CmqMessage<T>> pollNow(int count) {
        return null;
    }

    @Override
    public boolean commit() {
        return false;
    }
}
