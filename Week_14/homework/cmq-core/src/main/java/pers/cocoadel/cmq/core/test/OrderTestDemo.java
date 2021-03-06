package pers.cocoadel.cmq.core.test;

import lombok.SneakyThrows;
import pers.cocoadel.cmq.core.broker.AbstractCmqBroker;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.core.spi.CmqBrokerFactory;
import pers.cocoadel.cmq.core.spi.CmqConsumerFactory;
import pers.cocoadel.cmq.core.spi.CmqFactory;
import pers.cocoadel.cmq.core.spi.CmqProducerFactory;

public class OrderTestDemo {

    private static CmqBroker createCmqBroker() {
        CmqBrokerFactory cmqBrokerFactory = CmqBrokerFactory.getInstance();
        CmqBroker cmqBroker = cmqBrokerFactory.createCmqBroker();
        if (cmqBroker instanceof AbstractCmqBroker) {
            AbstractCmqBroker abstractCmqBroker = (AbstractCmqBroker) cmqBroker;
            CmqConsumerFactory cmqConsumerFactory = CmqConsumerFactory.getInstance();
            CmqProducerFactory cmqProducerFactory = CmqProducerFactory.getInstance();
            CmqFactory cmqFactory = CmqFactory.getInstance();
            abstractCmqBroker.setCmqFactory(cmqFactory);
            abstractCmqBroker.setCmqConsumerFactory(cmqConsumerFactory);
            abstractCmqBroker.setCmqProducerFactory(cmqProducerFactory);
        }
        return cmqBroker;
    }

    @SneakyThrows
    public static void main(String[] args) {


        String topic = "kk.test";
        CmqBroker broker = createCmqBroker();
        if (broker == null) {
            throw new IllegalStateException("create broker fail");
        }
        broker.createTopic(topic);
        Describe describe = new Describe(topic, null, "consumer1");
        CmqConsumer<Order> consumer = broker.createConsumer(describe);
        consumer.subscribe(topic);
        final boolean[] flag = new boolean[1];
        flag[0] = true;
        new Thread(() -> {
            while (flag[0]) {
                CmqMessage<Order> message = consumer.pollNow();
                if(null != message) {
                    System.out.println(message.getBody());
                    consumer.commit();
                }
            }
            System.out.println("程序退出。");
        }).start();

        CmqProducer producer = broker.createProducer();
        for (int i = 0; i < 1000; i++) {
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

        flag[0] = false;
    }
}
