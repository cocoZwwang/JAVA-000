package pers.cocoadel.cmq.core.producer;


import pers.cocoadel.cmq.core.message.CmqMessage;

public interface CmqProducer {

    boolean send(String topic, CmqMessage<?> message);
}
