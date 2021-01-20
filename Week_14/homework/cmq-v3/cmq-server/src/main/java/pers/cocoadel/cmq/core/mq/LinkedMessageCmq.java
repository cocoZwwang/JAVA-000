package pers.cocoadel.cmq.core.mq;


import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.Map;
import java.util.concurrent.*;

public class LinkedMessageCmq extends CmqSupport {
    private MessageNode head;

    private MessageNode tail;

    private int capacity;

    private int offsetWrite = 0;

    private final Map<String, MessageNode> consumerOffsetMap = new ConcurrentHashMap<>();

    public LinkedMessageCmq() {
    }

    @Override
    public void init(String topic, int capacity) {
        this.capacity = capacity;
        head = new MessageNode();
        tail = new MessageNode();
        head.setNext(tail);
        tail.setPrev(head);
    }

    @Override
    public boolean send(CmqMessage<?> message) {
        if (offsetWrite >= capacity) {
            throw new RuntimeException("capacity is full!");
        }
        MessageNode node = tail;
        node.setMessage(message);
        tail = new MessageNode();
        node.setNext(tail);
        tail.setPrev(node);
        return true;
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        MessageNode offset = consumerOffsetMap.computeIfAbsent(consumer,k -> head.getNext());
        return offset == tail ? null : offset.getMessage();
    }

    @Override
    public void commit(String consumer) {
        if (consumerOffsetMap.containsKey(consumer)) {
            MessageNode node = consumerOffsetMap.get(consumer);
            if (node != null && node != tail) {
                consumerOffsetMap.put(consumer, node.getNext());
            }
        }
    }
}
