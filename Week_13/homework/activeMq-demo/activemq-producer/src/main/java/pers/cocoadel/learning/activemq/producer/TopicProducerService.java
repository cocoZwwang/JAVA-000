package pers.cocoadel.learning.activemq.producer;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


public class TopicProducerService {
    private static final String url = "tcp://192.168.3.32:61616";

    public void send(){
        Destination destination = new ActiveMQTopic("test.topic");
        ActiveMQConnection conn = null;
        Session session = null;
        try {
            // 创建连接和会话
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
            conn = (ActiveMQConnection) factory.createConnection();
            conn.start();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            int index = 0;
            while (index++ < 100) {
                TextMessage message = session.createTextMessage(index + " message.");
                producer.send(message);
            }
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (session != null) {
                    session.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
