package pers.cocoadel.cmq.core.producer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.mq.Cmq;

public class GenericLocalCmqProducer implements CmqProducer {
    private CmqBroker cmqBroker;

    public GenericLocalCmqProducer(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }


    public GenericLocalCmqProducer() {

    }

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        Cmq cmq = cmqBroker.findMq(topic);
        if (cmq == null) {
            throw new IllegalArgumentException("Topic[" + topic + "] doesn't exist.");
        }
        return cmq.send(message);
    }

    @Override
    public void setCmqBroker(CmqBroker broker) {
        this.cmqBroker = broker;
    }
}
