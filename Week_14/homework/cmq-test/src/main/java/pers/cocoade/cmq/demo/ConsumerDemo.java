package pers.cocoade.cmq.demo;

import pers.cocoadel.cmq.client.spring.CmqTopicConnection;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.test.Order;

import java.util.concurrent.TimeUnit;

public class ConsumerDemo {
    // todo 如果重新启动会重新消费
    // todo 1.消费客户端起唯一名称？ 2、客户端缓存目前消费的offset
    public static void main(String[] args) {
        try {
            CmqTopicConnection cmqTopicConnection = new CmqTopicConnection();
            cmqTopicConnection.connect("localhost",8080);
            CmqConsumer<Order> consumer = cmqTopicConnection.createConsumer("order.test", null, Order.class);
            consumer.subscribe("order.test");
            while (true) {
                try {
                    CmqMessage<Order> message = consumer.pollNow();
                    if (message != null) {
                        System.out.println(message.getBody().toString());
                        //确认消费
                        consumer.commit();
                    } else {
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
