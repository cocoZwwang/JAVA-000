package pers.cocoadel.cmq.core.mq;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HashMapQueueCmq extends AbstractTopicCmq {

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

    @Override
    public boolean send(CmqMessage<?> message) {
        if (message == null) {
            return false;
        }
        if (offsetWriteable.get() > capacity) {
            log.error("the [topic: {}] queue is full,the capacity is {}",getTopic(),capacity);
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
        long readOffset = getReadOffset(consumer);
        if (readOffset < 0) {
            return null;
        }
        MessageNode node = queue.get(readOffset);
        if (node != null) {
            pollCountMap.put(consumer, pollCountMap.getOrDefault(consumer, 0) + 1);
        }
        return node == null ? null : node.getMessage();
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        long readOffset = getReadOffset(consumer);
        if (readOffset < 0) {
            return Collections.emptyList();
        }
        MessageNode curr = queue.get(readOffset);
        List<CmqMessage<?>> list = new LinkedList<>();
        while (curr != null && count-- > 0) {
            list.add(curr.getMessage());
            curr = curr.getNext();
        }
        List<CmqMessage<?>> result = ImmutableList.copyOf(list);
        pollCountMap.put(consumer, pollCountMap.getOrDefault(consumer, 0) + result.size());
        return result;
    }

    private long getReadOffset(String consumer) {
        long readOffset = readOffsetMap.computeIfAbsent(consumer, k -> offsetWriteable.get() - 1);
        if (readOffset < 0 && offsetWriteable.get() > 0) {
            readOffset = 0;
            readOffsetMap.put(consumer, 0L);
        }
        return readOffset;
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
    public long getOffset(String consumer) {
        return readOffsetMap.getOrDefault(consumer, -1L);
    }

    @Override
    public long getMqOffset() {
        return offsetWriteable.get() - 1;
    }

    @Override
    public void init(String topic, int capacity) {
        this.capacity = capacity;
    }
}
