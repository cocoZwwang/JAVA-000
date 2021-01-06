package per.cocoadel.learning.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import per.cocoadel.learning.demo.order.OrderService;
import per.cocoadel.learning.demo.product.ProductRepository;

@SpringBootApplication
public class OrderApplication implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    OrderService orderService;


    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        orderService.placeOrder(1l,8);
        orderService.placeOrder(1l,4);
    }
}
