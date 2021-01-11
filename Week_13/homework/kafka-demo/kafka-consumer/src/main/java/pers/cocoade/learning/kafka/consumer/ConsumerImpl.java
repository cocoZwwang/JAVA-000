package pers.cocoade.learning.kafka.consumer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


@Component
public class ConsumerImpl implements Consumer {
    private final String topic = "order.test";
    private final Properties properties;
    private final KafkaConsumer<String,String> consumer;
    private boolean flag = true;

    public ConsumerImpl() {
        this.properties = new Properties();
        properties.put("group.id","pers.ocoadel");
        properties.put("bootstrap.servers","192.168.3.32:9001,192.168.3.32:9002,192.168.3.32:9003");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void consumeOrder() {
        //订阅主题
        consumer.subscribe(Collections.singleton(topic));
        while (true) {
            try {
                ConsumerRecords<String,String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    Order order = JSON.parseObject(record.value(), Order.class);
                    System.out.println("receive order: " + order);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    consumer.commitAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() {
        if (flag) {
            flag = false;
        }
        consumer.close();
    }
}
