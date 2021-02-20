package pers.cocoadel.cmq.core.mq;

import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.*;

/**
 * 1）自定义内存Message数组模拟Queue。
 * 2）使用指针记录当前消息写入位置。
 * 3）对于每个命名消费者，用指针记录消费位置。
 */
public class ArrayMessageCmq implements RandomCmq {
    private CmqMessage<?>[] array;

    private int offsetWriteable = 0;

    private int maxCapacity;

    public ArrayMessageCmq() {

    }

    @Override
    public void init(String topic, int capacity) {
        this.maxCapacity = capacity;
        this.array = new CmqMessage[maxCapacity];
    }

    @Override
    public boolean send(CmqMessage<?> message) {
        if (offsetWriteable == maxCapacity) {
            return false;
        }
        array[offsetWriteable++] = message;
        return true;
    }

    @Override
    public CmqMessage<?> pollNow() {
        if (offsetWriteable == 0) {
            return null;
        }
        return array[offsetWriteable - 1];
    }

    @Override
    public CmqMessage<?> pollNow(String consumer) {
        if (offsetWriteable == 0) {
            return null;
        }
        return array[offsetWriteable - 1];
    }

    @Override
    public CmqMessage<?> read(int offset) {
        if (offset < 0 || offset >= offsetWriteable) {
            throw new IndexOutOfBoundsException(String.format("offset is < 0 || > %s ", offsetWriteable));
        }
        return array[offset];
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        return null;
    }

    @Override
    public void setOffset(String consumer, long offset) {

    }

    @Override
    public long getOffset(String consumer) {
        return 0;
    }

    @Override
    public long getMqOffset() {
        return offsetWriteable - 1;
    }

    /**
     * 消息确认
     * 创建 Consumer 实例的逻辑会保证同一名称的 Consumer 实例的唯一性
     * 所以这里同一名称的 Consumer 实例 commit 的线程安全和幂等性问题交给客户端处理
     */
    @Override
    public void commit(String consumer) {

    }
}
