package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HashMapQueueCmq implements Cmq {

    private long capacity = Long.MAX_VALUE - 1;

    private final Map<Long, MessageNode> queue = new ConcurrentHashMap<>();

    //队列写入数据的偏移量
    private final AtomicLong offset = new AtomicLong();

    //当前读取数据的偏移量
    private final Map<String, Long> readOffsetMap = new ConcurrentHashMap<>();

    //当前拉取了多少条数据
    //这是还没commit 的数据量
    //如果commit 后会被清空
    private final Map<String, Long> pollCountMap = new ConcurrentHashMap<>();

    // todo 测试并发的安全问题
    // todo 测试 synchronized 和 cas 哪个性能更加好
    @Override
    public boolean send(CmqMessage<?> message) {
        if (offset.get() == capacity || message == null) {
            return false;
        }
        MessageNode node = new MessageNode();
        node.setMessage(message);
        for(;;){
            long key = offset.get();
            MessageNode prev = queue.get(key);
            node.setPrev(prev);
            prev.setNext(node);
            queue.put(key + 1, node);
            if (offset.compareAndSet(key, key + 1)) {
                break;
            }
            prev.setNext(null);
            node.setPrev(null);
            queue.remove(key + 1, node);
        }
        return true;
    }

    @Override
    public CmqMessage<?> poll() {
        return null;
    }

    @Override
    public CmqMessage<?> poll(long timeOutMills) {
        return null;
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        return null;
    }

    @Override
    public CmqMessage<?> poll(String consumer, long timeOutMills) {
        return null;
    }

    @Override
    public void commit(String consumer) {

    }

    @Override
    public void setOffset(String consumer, int offset) {

    }

    @Override
    public void init(String topic, int capacity) {

    }
}
