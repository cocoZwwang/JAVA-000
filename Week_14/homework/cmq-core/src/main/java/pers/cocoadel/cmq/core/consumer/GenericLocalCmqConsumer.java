package pers.cocoadel.cmq.core.consumer;

import com.google.common.collect.ImmutableList;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.mq.Cmq;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class GenericLocalCmqConsumer<T> extends AbstractCmqConsumer<T> {

    private Cmq cmq;

    @Override
    public boolean subscribe(String topic) {
        cmq = getCmqBroker().findMq(topic);
        long offset = cmq.getMqOffset();
        cmq.setOffset(getDescribe().toString(),offset);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CmqMessage<T> pollNow() {
        checkNotNull(cmq);
        checkNotNull(getDescribe());
        return (CmqMessage<T>) cmq.pollNow(getDescribe().toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CmqMessage<T>> pollNow(int count) {
        checkNotNull(cmq);
        checkNotNull(getDescribe());
        List<CmqMessage<?>> list = cmq.pollNow(getDescribe().toString(), count);
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        List<CmqMessage<T>> result = list
                .stream()
                .map(cmqMessage -> (CmqMessage<T>) cmqMessage)
                .collect(Collectors.toList());
        return ImmutableList.copyOf(result);
    }

    @Override
    public boolean commit() {
        checkNotNull(cmq);
        checkNotNull(getDescribe());
        cmq.commit(getDescribe().toString());
        return true;
    }
}
