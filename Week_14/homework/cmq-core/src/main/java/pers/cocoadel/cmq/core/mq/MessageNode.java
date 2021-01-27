package pers.cocoadel.cmq.core.mq;

import lombok.Data;
import pers.cocoadel.cmq.core.message.CmqMessage;

@Data
public class MessageNode {

    private MessageNode prev;

    private MessageNode next;

    private CmqMessage<?> message;

}
