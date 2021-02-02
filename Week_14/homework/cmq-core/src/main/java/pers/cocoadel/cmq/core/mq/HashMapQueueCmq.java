package pers.cocoadel.cmq.core.mq;

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
public class HashMapQueueCmq extends CmqSupport {

    private long capacity = Long.MAX_VALUE - 1;

    private final Map<Long, MessageNode> queue = new ConcurrentSkipListMap<>();

    //队列写入数据的偏移量
    private final AtomicLong writeOffset = new AtomicLong();

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
        if (writeOffset.get() == capacity || message == null) {
            return false;
        }
        MessageNode node = new MessageNode();
        node.setMessage(message);
        synchronized (lock) {
            long key = writeOffset.get();
            MessageNode prev = queue.get(key);
            if (prev != null) {
                node.setPrev(prev);
                prev.setNext(node);
            }
            queue.put(writeOffset.incrementAndGet(), node);
            lock.notifyAll();
        }
        return true;
    }

    @Override
    public CmqMessage<?> poll() {
        return super.poll();
    }

    @Override
    public CmqMessage<?> poll(long timeOutMills) {
        return super.poll(timeOutMills);
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        long readOffset = readOffsetMap.computeIfAbsent(consumer, k -> writeOffset.get());
        MessageNode prev = queue.get(readOffset);
        if (prev == null) {
            return null;
        }
        pollCountMap.put(consumer, 1);
        return prev.getMessage();
    }

    @Override
    public CmqMessage<?> poll(String consumer, long timeOutMills) {
        CmqMessage<?> message = poll(consumer);
        if (message == null) {
            synchronized (lock) {
                try {
                    lock.wait(timeOutMills);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message = poll(consumer);
        }
        return message;
    }

    @Override
    public List<CmqMessage<?>> poll(String consumer, int count) {
        long readOffset = readOffsetMap.computeIfAbsent(consumer, k -> writeOffset.get());
        MessageNode prev = queue.get(readOffset);
        if (prev == null) {
            return Collections.emptyList();
        }
        List<CmqMessage<?>> result = new LinkedList<>();
        int c = 0;
        MessageNode curr = prev;
        while (c++ < count && curr != null) {
            result.add(curr.getMessage());
            curr = curr.getNext();
        }
        pollCountMap.put(consumer, c);
        return result;
    }

    @Override
    public List<CmqMessage<?>> poll(String consumer, int count, long timeOutMills) {
        List<CmqMessage<?>> result = poll(consumer, count);
        long time = timeOutMills;
        while (result.size() < count && time > 0) {
            long startTime = System.currentTimeMillis();
            synchronized (lock) {
                try {
                    lock.wait(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count = count - result.size();
                List<CmqMessage<?>> list = poll(consumer,count);
                result.addAll(list);
                time = time - (System.currentTimeMillis() - startTime);
            }
        }
        return result;
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
    public void setOffset(String consumer,  long offset) {
        long writeIndex = writeOffset.get();
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
