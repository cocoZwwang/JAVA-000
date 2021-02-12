package pers.cocoadel.cmq.client.netty;

import lombok.Data;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;

@Data
public class NettyClientProducer implements CmqProducer {

    private NettyCmqClient nettyCmqClient;

    private String token;

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        return false;
    }
}
