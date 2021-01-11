package pers.cocoadel.learning.kafka.producer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ProducerImpl implements Producer {
    private final Properties properties;

    private final KafkaProducer<String,String> producer;

    private final String topic = "order.test";

    public ProducerImpl() {
        properties = new Properties();
        properties.put("bootstrap.servers","192.168.3.32:9001,192.168.3.32:9002,192.168.3.32:9003");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void send(Order order) {
        try {
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(topic, order.getId().toString(), JSON.toJSONString(order));
            producer.send(producerRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        producer.close();
    }
}
