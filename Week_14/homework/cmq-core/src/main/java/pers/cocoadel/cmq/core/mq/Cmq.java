package pers.cocoadel.cmq.core.mq;


import pers.cocoadel.cmq.core.message.CmqMessage;

import java.util.List;

public interface Cmq  {
    /**
     * 发送消息到队列
     * @param message 消息
     * @return 是否存储成功
     */
    boolean send(CmqMessage<?> message);

    /**
     * 拉取队列最新一条消息，没有则返回 null
     * @return 最新的一条消息
     */
    CmqMessage<?> pollNow();

    /**
     * 读取当前消费者偏移量位置的消息，并且返回
     * 如果当前偏移量为队列末端，则立马返回 null
     * @param consumer 消费者标记
     * @return 当前消费者偏移位置的消息
     */
    CmqMessage<?> pollNow(String consumer);

    /**
     * 从当前消费者偏移量位置开始读取，最多一共 count 条消息
     * 如果读取的消息不够 count 条，则直接放回当前读取的消息
     * @param consumer 消费者标记
     * @param count 读取的消息数量
     * @return 返回消息列表，列表长度可能为 0
     */
    List<CmqMessage<?>> pollNow(String consumer, int count);

//    /**
//     * 拉取队列中最新一条消息
//     * 如果队列没有最新消息，则阻塞当前线程
//     */
//    CmqMessage<?> poll();
//
//    /**
//     * 拉取队列中最新一条消息
//     * 如果队列为空，则阻塞当前线程 timeOutMills
//     * @return 返回拉取的最新消息，如果超时则返回 null
//     */
//    CmqMessage<?> poll(long timeOutMills);
//
//
//    /**
//     * 拉取消费者当前偏移量的消息
//     * 如果队列没有最新消息，则阻塞当前线程
//     * @param consumer 消费者描述，唯一标记
//     * @return 返回拉取的消息
//     */
//    CmqMessage<?> poll(String consumer);
//
//    /**
//     * 拉取消费者当前偏移量的消息
//     * 如果队列没有最新消息，则阻塞当前线程 timeOutMills
//     * @param consumer consumer 消费者描述，唯一标记
//     * @param timeOutMills 阻塞的时间限制
//     * @return 返回拉取的消息，如果超时则返回 null
//     */
//    CmqMessage<?> poll(String consumer,long timeOutMills);
//
//    /**
//     * 拉取从消费者当前偏移量开始，一共最多 count 条消息
//     * 如果消息数不够 count，则阻塞当前线程
//     * @param consumer 消费者描述，唯一标记
//     * @param count 拉取消息的数量
//     * @return 返回消息列表
//     */
//    List<CmqMessage<?>> poll(String consumer, int count);
//
//    /**
//     * 拉取从消费者当前偏移量开始，一共最多 count 条消息
//     * 如果消息数不够 count，则阻塞当前线程 timeOutMills
//     * @param consumer 消费者描述，唯一标记
//     * @param count 拉取消息的数量
//     * @param timeOutMills 阻塞的时间限制
//     * @return 返回消息列表，如果超时则返回当前已经拉取的数量，链表元素可能是0
//     */
//    List<CmqMessage<?>> poll(String consumer, int count,long timeOutMills);

    /**
     * 确认消费，当前消费者的偏移量更新到最新拉取的位置
     * @param consumer 消费者描述，唯一标记
     */
    void commit(String consumer);


    /**
     * 设置消费者的偏移量
     * @param consumer 消费者描述，唯一标记
     * @param offset 偏移量
     */
    void setOffset(String consumer, long offset);

    long getOffset(String consumer);

    long getMqOffset();

    /**
     * CMQ 初始化
     * @param topic 主题
     * @param capacity 队列的容量
     */
    void init(String topic, int capacity);
}
