package pers.cocoadel.learning.hmily.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.learning.hmily.order.domain.Order;
import pers.cocoadel.learning.hmily.order.domain.Product;
import pers.cocoadel.learning.hmily.order.service.OrderServiceImpl;
import pers.cocoadel.learning.hmily.order.client.ProductClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class OrderApplication implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ProductClient productClient;

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        testTccSuccess();
        testTccRollback();
    }

    /**
     * 测试TCC事务成功
     */
    private void testTccSuccess(){
        try {
            //打印更新前信息
            TimeUnit.SECONDS.sleep(3);
            Order order = orderService.showOrder(11L);
            Product product = productClient.findById(order.getProductId());
            log.info("原来Order[id:{}]状态:{}",order.getId(),order.getState());
            log.info("原来Product[id:{}]库存:{}",product.getId(),product.getStock());
            //更新
            orderService.orderPay(order);
            //打印更新后信息
            TimeUnit.SECONDS.sleep(1);
            order = orderService.showOrder(order.getId());
            product = productClient.findById(order.getProductId());;
            log.info("原来Order[id:{}]状态:{}",order.getId(),order.getState());
            log.info("更新后Product[id:{}]库存:{}",product.getId(),product.getStock());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 测试TCC事务成功
     */
    private void testTccRollback(){
        Order order = null;
        Product product = null;
        try {
            //打印更新前信息
            TimeUnit.SECONDS.sleep(3);
            order = orderService.showOrder(12L);
            product = productClient.findById(order.getProductId());
            log.info("原来Order[id:{}]状态:{}",order.getId(),order.getState());
            log.info("原来Product[id:{}]库存:{}",product.getId(),product.getStock());
            //更新但是会抛异常
            orderService.orderPayAndThrowException(order);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //打印更新后信息
            try {
                TimeUnit.SECONDS.sleep(1);
                order = orderService.showOrder(order.getId());
                product = productClient.findById(order.getProductId());;
                log.info("原来Order[id:{}]状态:{}",order.getId(),order.getState());
                log.info("更新后Product[id:{}]库存:{}",product.getId(),product.getStock());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
