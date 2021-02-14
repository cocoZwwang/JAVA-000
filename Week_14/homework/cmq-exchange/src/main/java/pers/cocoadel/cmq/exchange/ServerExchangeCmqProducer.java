package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.exception.CmqOperationException;
import pers.cocoadel.cmq.comm.request.ProducerRequestBody;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerExchangeCmqProducer extends ExchangeCmqProducer {

    private CmqBroker cmqBroker;

    private final Map<Describe, CmqProducer> producerMap = new ConcurrentHashMap<>();

    public void setBroker(CmqBroker broker) {
        this.cmqBroker = broker;
    }


    @Override
    public void send(SendTextRequestBody requestBody) {
        try {
            requestBody.check();
            CmqProducer producer = createCmqProducer(requestBody);
            Describe describe = requestBody.getDescribe();
            producer.send(describe.getTopic(), requestBody.getBody());
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

    @Override
    public void removeProducer(Describe describe) {
        producerMap.remove(describe);
    }


    protected CmqProducer createCmqProducer(ProducerRequestBody requestBody) {
        Describe describe = requestBody.getDescribe();
        return producerMap.computeIfAbsent(describe, k -> cmqBroker.createProducer());
    }
}
