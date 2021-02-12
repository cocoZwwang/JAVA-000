package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.Describe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ExchangeCmqConsumer<T> {
    protected CmqBroker cmqBroker;

    protected boolean autoCreateTopic = true;

    private final Map<Describe, CmqConsumer<T>> localConsumerMap = new ConcurrentHashMap<>();

    protected CmqConsumer<T> createConsumer(Describe describe) {
        checkNotNull(describe);
        if (cmqBroker.findMq(describe.getTopic()) == null && autoCreateTopic) {
            cmqBroker.createTopic(describe.getTopic());
        }
        return localConsumerMap.computeIfAbsent(describe,k-> cmqBroker.createConsumer(describe));
    }

    public void removeConsumer(Describe describe) {
        localConsumerMap.remove(describe);
    }

    public abstract void subscribe(ConsumerRequestBody requestBody);

    public abstract PollResponseBody poll(PollRequestBody requestBody);

    public abstract void commit(ConsumerRequestBody cmqRequest);

    public void setCmqBroker(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }


}
