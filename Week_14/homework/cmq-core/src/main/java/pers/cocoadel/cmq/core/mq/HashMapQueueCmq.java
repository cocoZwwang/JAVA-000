package pers.cocoadel.cmq.core.mq;

import com.google.common.collect.ImmutableList;
import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 这里使用序列号记录节点的位置，主要是为了客户端能持久化当前读取的偏移量
 */
public class HashMapQueueCmq implements Cmq {

    private long capacity = Long.MAX_VALUE - 1;

    private final Map<Long, MessageNode> queue = new ConcurrentSkipListMap<>();

    //队列写入数据的偏移量
    private final AtomicLong offsetWriteable = new AtomicLong(0);

    //当前读取数据的偏移量
    private final Map<String, Long> readOffsetMap = new ConcurrentHashMap<>();

    //当前拉取了多少条数据
    //这是还没commit 的数据量
    //如果commit 后会被清空
    private final Map<String, Integer> pollCountMap = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    // todo 测试并发的安全问题
    // todo 测试 synchronized 和 cas 哪个性能更加好
    @Override
    public boolean send(CmqMessage<?> message) {
        if (offsetWriteable.get() > capacity || message == null) {
            return false;
        }
        MessageNode node = new MessageNode();
        node.setMessage(message);
        synchronized (lock) {
            long key = offsetWriteable.get();
            MessageNode prev = queue.get(key - 1);
            if (prev != null) {
                node.setPrev(prev);
                prev.setNext(node);
            }
            queue.put(offsetWriteable.getAndIncrement(), node);
            lock.notifyAll();
        }
        return true;
    }

    @Override
    public CmqMessage<?> pollNow() {
        if (offsetWriteable.get() == 0) {
            return null;
        }
        return queue.get(offsetWriteable.get() - 1).getMessage();
    }

    @Override
    public CmqMessage<?> pollNow(String consumer) {
        long readOffset = readOffsetMap.computeIfAbsent(consumer, k -> offsetWriteable.get() - 1);
        if (readOffset < 0) {
            return null;
        }
        MessageNode node = queue.get(readOffset);
        return node == null ? null : node.getMessage();
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        long readOffset = readOffsetMap.computeIfAbsent(consumer, k -> offsetWriteable.get() - 1);
        if (readOffset < 0) {
            return Collections.emptyList();
        }
        MessageNode curr = queue.get(readOffset);
        List<CmqMessage<?>> list = new LinkedList<>();
        while (curr != null && count-- > 0) {
            list.add(curr.getMessage());
            curr = curr.getNext();
        }
        return ImmutableList.copyOf(list);
    }

    @Override
    public void commit(String consumer) {
        Integer count = pollCountMap.get(consumer);
        if (count != null && readOffsetMap.containsKey(consumer)) {
            readOffsetMap.put(consumer, readOffsetMap.get(consumer) + count);
        }
        pollCountMap.remove(consumer);
    }

    @Override
    public void setOffset(String consumer, long offset) {
        long writeIndex = offsetWriteable.get();
        if (offset > writeIndex) {
            String message = String.format("offset(%s) is bigger than queue size(%s)", offset, writeIndex);
            throw new IndexOutOfBoundsException(message);
        }
        pollCountMap.remove(consumer);
        readOffsetMap.put(consumer, offset);
    }

    @Override
    public void init(String topic, int capacity) {
        this.capacity = capacity;
    }
}
