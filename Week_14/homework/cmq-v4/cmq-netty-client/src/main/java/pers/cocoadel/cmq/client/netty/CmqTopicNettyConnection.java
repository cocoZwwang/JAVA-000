package pers.cocoadel.cmq.client.netty;

import com.google.common.base.Strings;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.connection.Connection;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import java.io.IOException;

@Slf4j
public class CmqTopicNettyConnection implements Connection  {

    private  NettyCmqClient nettyCmqClient;

    @Override
    public void connect(String ip, int port) throws IOException {
        try {
            doClose();
            nettyCmqClient = new NettyCmqClient(ip, port);
            nettyCmqClient.start();
            log.info("connection success!");
        } catch (Exception e) {
            log.error("connection fail: " + e.getMessage());
        }
    }

    @Override
    public void disConnect() throws IOException {
        doClose();
    }

    private void doClose() {
        if (nettyCmqClient != null) {
            nettyCmqClient.close();
        }
    }

    @Override
    public <T> CmqConsumer<T> createConsumer(String topic, String groupId, Class<T> tClass) {
        if (Strings.isNullOrEmpty(topic)) {
            throw new NullPointerException("the topic is null");
        }
        if (nettyCmqClient.isActive()) {
            Describe describe = new Describe();
            describe.setTopic(topic);
            describe.setGroupId(groupId);
            ChannelId id = nettyCmqClient.getChannelId();
            describe.setName(id.asLongText());
            NettyClientConsumer<T> consumer = new NettyClientConsumer<>();
            consumer.setDescribe(describe);
            consumer.setNettyCmqClient(nettyCmqClient);
            consumer.setTClass(tClass);
            return consumer;
        }
        throw new IllegalStateException("create consumer error: channel is un active");
    }

    @Override
    public CmqProducer createProducer() {
        NettyClientProducer producer = new NettyClientProducer();
        producer.setNettyCmqClient(nettyCmqClient);
        return producer;
    }
}
