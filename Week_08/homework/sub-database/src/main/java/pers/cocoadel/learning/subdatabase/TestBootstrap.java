package pers.cocoadel.learning.subdatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.learning.subdatabase.domain.Order;
import pers.cocoadel.learning.subdatabase.service.OrderService;

import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootApplication
public class TestBootstrap implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(TestBootstrap.class,args);
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        orderService.insertOrders(10000);
        //打印
        orderService.showByUserId(1);
        log.info("-----------------------------------------------");
        //更新
        List<Order> orders = orderService.selectOrders(1);
        Order order = orders.get(0);
        order.setState(1);
        orderService.update(order);
        log.info("update order[id:{}] state success",order.getId());
        //删除
        Order deleteOrder = orders.get(1);
        orderService.delete(Collections.singleton(deleteOrder.getId()),deleteOrder.getUserId());
        log.info("delete order[id:{}] success",deleteOrder.getId());
        //打印
        orderService.showByUserId(1);

//        orderService.deleteAll();
    }

}
