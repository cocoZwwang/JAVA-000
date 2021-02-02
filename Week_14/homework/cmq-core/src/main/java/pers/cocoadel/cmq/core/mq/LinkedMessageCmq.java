package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 基于链表实现的队列，通过记录节点来记录消费的 offset
 */
public class LinkedMessageCmq extends CmqSupport {
    private MessageNode head;

    private MessageNode tail;

    private int capacity;

    private int offsetWrite = 0;

    private final Map<String, MessageNode> consumerOffsetMap = new ConcurrentHashMap<>();

    private final Map<String, MessageNode> readOffsetMap = new ConcurrentHashMap<>();

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
    public synchronized boolean send(CmqMessage<?> message) {
        if (offsetWrite >= capacity) {
            throw new RuntimeException("capacity is full!");
        }
        MessageNode node = tail;
        node.setMessage(message);
        tail = new MessageNode();
        node.setNext(tail);
        tail.setPrev(node);
        offsetWrite++;
        return true;
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        MessageNode offset = consumerOffsetMap.computeIfAbsent(consumer,key -> head.getNext());
        if (offset == tail) {
            return null;
        }
        readOffsetMap.put(consumer, offset);
        return offset.getMessage();
    }

    @Override
    public List<CmqMessage<?>> poll(String consumer, int count,long timeOutMills) {
        MessageNode prev = consumerOffsetMap.computeIfAbsent(consumer,key -> head.getNext());
        while (prev == null) {

        }
        if (prev == tail) {
            return Collections.emptyList();
        }
        List<CmqMessage<?>> result = new LinkedList<>();
        int c = 0;
        MessageNode curr = prev;
        while (c++ < count && curr != tail) {
            result.add(curr.getMessage());
            curr = curr.getNext();
        }
        readOffsetMap.put(consumer, curr.getPrev());
        return result;
    }

    @Override
    public void commit(String consumer) {
        MessageNode readOffset = readOffsetMap.get(consumer);
        if (consumerOffsetMap.containsKey(consumer)) {
            consumerOffsetMap.put(consumer, readOffset);
        }
        readOffsetMap.remove(consumer);
    }
}

