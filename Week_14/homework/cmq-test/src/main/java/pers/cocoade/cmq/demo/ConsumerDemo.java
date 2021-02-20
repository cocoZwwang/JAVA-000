package pers.cocoade.cmq.demo;

import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.client.netty.CmqTopicNettyConnection;
import pers.cocoadel.cmq.client.spring.SpringCmqTopicConnection;
import pers.cocoadel.cmq.connection.Connection;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.test.Order;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ConsumerDemo {
    // todo 如果重新启动会重新消费
    // todo 1.消费客户端起唯一名称？ 2、客户端缓存目前消费的offset
    public static void main(String[] args) {
        try {
//            Connection connection = new SpringCmqTopicConnection();
            Connection connection = new CmqTopicNettyConnection();
            connection.connect("localhost", 8088);
            CmqConsumer<Order> consumer = connection.createConsumer("order.test", null, Order.class);
            boolean subscribeResult = consumer.subscribe("order.test");
            log.info("subscribeResult: " + subscribeResult);
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

    private static Connection springConnection() {
        return new SpringCmqTopicConnection();
    }

    private static Connection nettyConnection() {
        return new CmqTopicNettyConnection();
    }

}
