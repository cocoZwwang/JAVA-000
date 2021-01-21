package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.exception.CmqOperationException;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerExchangeCmqProducer extends ExchangeCmqProducer {

    private CmqBroker cmqBroker;

    private final Map<ProducerKey, CmqProducer> producerMap = new ConcurrentHashMap<>();

    public void setBroker(CmqBroker broker) {
        this.cmqBroker = broker;
    }


    @Override
    public void send(SendTextRequestBody requestBody) {
        requestBody.check();
        try {
            CmqProducer producer = createCmqProducer(requestBody);
            producer.send(requestBody.getTopic(), requestBody.getBody());
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

    @Override
    public void removeProducer(ProducerKey key) {
        producerMap.remove(key);
    }


    protected CmqProducer createCmqProducer(CommRequestBody requestBody) {
        ProducerKey key = new ProducerKey(requestBody.getToken());
        return producerMap.computeIfAbsent(key, k -> cmqBroker.createProducer());
    }
}
