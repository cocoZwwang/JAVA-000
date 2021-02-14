package pers.cocoade.cmq.demo;

import pers.cocoadel.cmq.client.netty.CmqTopicNettyConnection;
import pers.cocoadel.cmq.client.spring.SpringCmqTopicConnection;
import pers.cocoadel.cmq.connection.Connection;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.test.Order;

public class ProducerDemo {
    private static final String topic = "order.test";

    public static void main(String[] args) {
        try {
//            Connection connection = new SpringCmqTopicConnection();
            Connection connection = nettyConnection();
            connection.connect("localhost",8088);
            CmqProducer producer = connection.createProducer();
            for (int i = 0; i < 10; i++) {
                Order order = new Order(1000L + i, System.currentTimeMillis(), "USD2CNY", 6.51d);
                producer.send(topic, new GenericCmqMessage<>(null, order));
            }

            Thread.sleep(500);
            System.out.println("点击任何键，发送一条消息；点击q或e，退出程序。");
            while (true) {
                char c = (char) System.in.read();
                if(c > 20) {
                    System.out.println(c);
                    producer.send(topic, new GenericCmqMessage<>(null,
                            new Order(100000L + c, System.currentTimeMillis(), "USD2CNY", 6.52d)));
                }

                if( c == 'q' || c == 'e') break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection springConnection() {
        return new SpringCmqTopicConnection();
    }

    private static Connection nettyConnection() {
        return new CmqTopicNettyConnection();
    }
}
