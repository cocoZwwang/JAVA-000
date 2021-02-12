package pers.cocoadel.cmq.core.mq;

import com.google.common.collect.ImmutableList;
import pers.cocoadel.cmq.core.message.CmqMessage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 基于链表实现的队列，通过记录节点来记录消费的 offset
 */
public class LinkedMessageCmq implements Cmq {
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
    public CmqMessage<?> pollNow() {
        if (tail.getPrev() == head) {
            return null;
        }
        return tail.getPrev().getMessage();
    }

    @Override
    public CmqMessage<?> pollNow(String consumer) {
        MessageNode offset = consumerOffsetMap.computeIfAbsent(consumer,key -> head.getNext());
        if (offset == tail) {
            return null;
        }
        readOffsetMap.put(consumer, offset);
        return offset.getMessage();
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        MessageNode prev = consumerOffsetMap.computeIfAbsent(consumer,key -> head.getNext());
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
        return ImmutableList.copyOf(result);
    }

    @Override
    public void commit(String consumer) {
        MessageNode readOffset = readOffsetMap.get(consumer);
        if (consumerOffsetMap.containsKey(consumer)) {
            consumerOffsetMap.put(consumer, readOffset);
        }
        readOffsetMap.remove(consumer);
    }

    @Override
    public void setOffset(String consumer, long offset) {
        checkArgument(offset < offsetWrite);
        MessageNode curr = head;
        while (offset-- >= 0 && curr.getNext() != tail) {
            curr = curr.getNext();
        }
        consumerOffsetMap.put(consumer, curr);
        readOffsetMap.remove(consumer);
    }
}

