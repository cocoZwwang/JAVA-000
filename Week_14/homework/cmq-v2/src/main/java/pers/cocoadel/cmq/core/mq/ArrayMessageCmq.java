package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1）自定义内存Message数组模拟Queue。
 * 2）使用指针记录当前消息写入位置。
 * 3）对于每个命名消费者，用指针记录消费位置。
 */
public class ArrayMessageCmq extends CmqSupport {
    private CmqMessage<?>[] array;

    private int offsetWrite = 0;

    private int maxCapacity;

    private final Map<String, Integer> consumerOffsetMap = new ConcurrentHashMap<>();

    public ArrayMessageCmq() {

    }

    @Override
    public void init(String topic, int capacity) {
        this.maxCapacity = capacity;
        this.array = new CmqMessage[maxCapacity];
    }

    @Override
    public boolean send(CmqMessage<?> message) {
        if (offsetWrite == maxCapacity) {
            return false;
        }
        array[offsetWrite++] = message;
        return true;
    }

    @Override
    public CmqMessage<?> poll(String consumer) {
        int offsetRead = consumerOffsetMap.computeIfAbsent(consumer, k -> 0);
        if (offsetRead >= offsetWrite) {
            return null;
        }
        return array[offsetRead];
    }

    @Override
    public void setOffset(String consumer, int offset) {
        if (offset >= offsetWrite) {
            throw new IndexOutOfBoundsException("offset is out of size of messages");
        }
        if (!consumerOffsetMap.containsKey(consumer)) {
            throw new NoSuchElementException(String.format("the consumer{%s} is not exist！", consumer));
        }
        consumerOffsetMap.put(consumer, offset);
    }

    /**
     * 消息确认
     * 创建 Consumer 实例的逻辑会保证同一名称的 Consumer 实例的唯一性
     * 所以这里同一名称的 Consumer 实例 commit 的线程安全和幂等性问题交给客户端处理
     */
    @Override
    public void commit(String consumer) {
        if (consumerOffsetMap.containsKey(consumer)) {
            if (consumerOffsetMap.get(consumer) < offsetWrite) {
                consumerOffsetMap.put(consumer, consumerOffsetMap.get(consumer) + 1);
            }
        }
    }
}