package pers.cocoadel.cmq.core.mq;


import lombok.Data;
import pers.cocoadel.cmq.core.message.CmqMessage;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于阻塞队列，简单的出列和入列操作
 * 不支持基于具体消费者的便宜量拉取消息
 */
@Data
public class LocalBlockingQueueCmq implements Cmq {
    private String topic;

    private BlockingQueue<CmqMessage<?>> queue;

    @Override
    public void init(String topic, int capacity) {
        this.topic = topic;
        queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public boolean send(CmqMessage<?> message) {
        return queue.offer(message);
    }

    @Override
    public CmqMessage<?> pollNow() {
        return queue.poll();
    }

    @Override
    public CmqMessage<?> pollNow(String consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CmqMessage<?>> pollNow(String consumer, int count) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void commit(String consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOffset(String consumer, long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getOffset(String consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMqOffset() {
        return queue.size() - 1;
    }
}
