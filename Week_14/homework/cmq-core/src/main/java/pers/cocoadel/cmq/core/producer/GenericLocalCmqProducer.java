package pers.cocoadel.cmq.core.producer;

import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.mq.Cmq;

import static com.google.common.base.Preconditions.checkNotNull;

public class GenericLocalCmqProducer extends AbstractCmqProducer {

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        CmqBroker cmqBroker = getCmqBroker();
        checkNotNull(cmqBroker);
        Cmq cmq = cmqBroker.findMq(topic);
        return cmq.send(message);
    }
}
