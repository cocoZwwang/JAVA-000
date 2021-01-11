package pers.cocoadel.learning.activemq.consumer;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ConsumerService {
    private static final String url = "tcp://192.168.3.32:61616";

    private final Object lock = new Object();

    @PostConstruct
    public void startListener() {
        Destination destination = new ActiveMQTopic("test.topic");
        ActiveMQConnection conn = null;
        Session session = null;
        try {
            // 创建连接和会话
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
            conn = (ActiveMQConnection) factory.createConnection();
            conn.start();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createConsumer(destination);
            AtomicLong count = new AtomicLong(0);
            MessageListener listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.printf("[%s]receive message from mq:%s\n", count.incrementAndGet(), message);
                }
            };
            consumer.setMessageListener(listener);
            synchronized (lock) {
                lock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }

                if (session != null) {
                    session.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        lock.notify();
    }
}
