package pers.cocoadel.cmq.connection;

import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.producer.CmqProducer;

import java.io.IOException;

public interface Connection {
    void connect(String ip, int port) throws IOException;

    void disConnect() throws IOException;

    <T> CmqConsumer<T> createConsumer(String topic, String groupId, Class<T> tClass);

    CmqProducer createProducer();

}
