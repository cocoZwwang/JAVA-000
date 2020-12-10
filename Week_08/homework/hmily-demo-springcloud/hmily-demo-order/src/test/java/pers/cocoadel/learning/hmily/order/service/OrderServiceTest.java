package pers.cocoadel.learning.hmily.order.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.cocoadel.learning.hmily.order.client.ProductClient;
import pers.cocoadel.learning.hmily.order.domain.Order;
import pers.cocoadel.learning.hmily.order.domain.Product;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTest {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ProductClient productClient;

    @Test
    public void update() {
        Order order = orderService.showOrder(1L);
        order.setState(1);
        Product product = productClient.findById(order.getProductId());
        System.out.println(product);
        orderService.orderPay(order);
        orderService.showOrder(order.getId());
        product = productClient.findById(order.getProductId());
        System.out.println(product);
    }

}