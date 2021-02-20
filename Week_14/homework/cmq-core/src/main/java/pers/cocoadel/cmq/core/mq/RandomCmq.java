package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

public interface RandomCmq extends Cmq {

    CmqMessage<?> read(int offset);
}
