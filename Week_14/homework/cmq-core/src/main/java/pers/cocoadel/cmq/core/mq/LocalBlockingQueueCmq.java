package pers.cocoadel.cmq.core.mq;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class LocalBlockingQueueCmq extends CmqSupport {
    private String topic;

    private BlockingQueue<CmqMessage<?>> queue;

    public LocalBlockingQueueCmq(String topic, int capacity) {
        init(topic,capacity);
    }

    public LocalBlockingQueueCmq() {

    }

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
    public CmqMessage<?> poll() {
        return queue.poll();
    }

    @SneakyThrows
    @Override
    public CmqMessage<?> poll(long timeOutMills) {
        return queue.poll(timeOutMills, TimeUnit.MILLISECONDS);
    }
}
