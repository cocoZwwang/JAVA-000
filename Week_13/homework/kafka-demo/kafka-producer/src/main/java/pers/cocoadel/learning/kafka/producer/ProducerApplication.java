package pers.cocoadel.learning.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class ProducerApplication implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ProducerService producerService;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        List<Order>  orders = createList();
        for (Order order : orders) {
            producerService.sendOrder(order);
        }
    }

    private List<Order> createList(){
        List<Order> orders = new LinkedList<>();
        for (int i = 1; i <= 10; i++) {
            Order order = new Order();
            order.setId((long) i);
            order.setPrice(10.0d);
            order.setTs((long) i);
            order.setSymbol("symbol-" + i);
            orders.add(order);
        }
        return orders;
    }
}
