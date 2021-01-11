package pers.cocoade.learning.kafka.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class KafkaMessageController {

    @KafkaListener(topics = "order.test",groupId = "pers.ocoadel")
    public void consumerOrder(@Payload String message) {
        Order order = JSON.parseObject(message, Order.class);
        log.info("receive order: " + order);
    }

}
