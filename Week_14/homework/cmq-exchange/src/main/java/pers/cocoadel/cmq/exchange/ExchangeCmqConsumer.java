package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class ExchangeCmqConsumer<T> {
    protected CmqBroker cmqBroker;

    private final Map<ConsumerKey, CmqConsumer<T>> localConsumerMap = new ConcurrentHashMap<>();

    protected CmqConsumer<T> createConsumer(String topic,String token) {
        ConsumerKey key = new ConsumerKey(token,topic,null);
        return localConsumerMap.computeIfAbsent(key,k-> cmqBroker.createConsumer(topic, key.toString()));
    }

    public void removeConsumer(ConsumerKey key) {
        localConsumerMap.remove(key);
    }

    public void removeConsumer(String token) {
        Set<ConsumerKey> consumerKeys = localConsumerMap
                .keySet()
                .stream()
                .filter(key -> key.getToken().equals(token))
                .collect(Collectors.toSet());
        for (ConsumerKey key : consumerKeys) {
            removeConsumer(key);
        }
    }

    public abstract void subscribe(CommRequestBody requestBody);

    public abstract PollResponseBody poll(PollRequestBody requestBody);

    public abstract void commit(CommRequestBody cmqRequest);

    public void setCmqBroker(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }


}
