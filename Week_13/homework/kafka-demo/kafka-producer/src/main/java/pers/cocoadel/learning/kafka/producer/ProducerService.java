package pers.cocoadel.learning.kafka.producer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProducerService {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public void sendOrder(Order order) {
        try {
            send("order.test", order);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void send(String topic, Object msg) throws IOException {
        ProducerRecord<String, String> pr = new ProducerRecord<>(topic, JSON.toJSONString(msg));
        kafkaTemplate.send(pr);
    }
}
