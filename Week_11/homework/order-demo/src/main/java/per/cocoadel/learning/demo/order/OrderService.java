package per.cocoadel.learning.demo.order;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cocoadel.learning.demo.subscrib.DeliverProductEvent;
import per.cocoadel.learning.demo.subscrib.EventType;
import per.cocoadel.learning.demo.subscrib.OrderStateEvent;
import pers.cocoadel.learning.redis.core.RedisOperator;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final RedisOperator redisOperator;

    private final AtomicLong idCreator = new AtomicLong(0);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, RedisOperator redisOperator) {
        this.orderRepository = orderRepository;
        this.redisOperator = redisOperator;
    }

    /**
     * 订阅仓库出货通知
     */
    @PostConstruct
    public void init() {
        JedisPubSub deliverProductSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                LOGGER.info(String.format("receive message from channel(%s): %s", channel, message));
                if (EventType.ProductEvent.DELIVER_PRODUCT_EVENT.equals(channel)) {
                    DeliverProductEvent deliverProductEvent = JSON.parseObject(message, DeliverProductEvent.class);
                    if ("OK".equals(deliverProductEvent.getMessage())) {
                        completedOrder(deliverProductEvent.getOrderId());
                    } else {
                        failOrder(deliverProductEvent.getOrderId(), deliverProductEvent.getMessage());
                    }

                }
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                super.onSubscribe(channel, subscribedChannels);
            }
        };
        executorService.submit(() ->
                redisOperator.subscribe(deliverProductSub, EventType.ProductEvent.DELIVER_PRODUCT_EVENT));
    }

    public void placeOrder(long productId, int amount) {
        //生成订单
        Order order = new Order();
        order.setId(idCreator.incrementAndGet());
        order.setProductId(productId);
        order.setProductAmount(amount);
        order.setUserId(1L);
        order.setState(OrderState.PAYED.getCode());
        orderRepository.addOrder(order);
        LOGGER.info("placeOrder: " + order);
        //发生订单生成消息
        OrderStateEvent orderStateEvent = new OrderStateEvent();
        orderStateEvent.setOrderId(order.getId());
        orderStateEvent.setOrderState(order.getState());
        redisOperator.publish(EventType.OrderEvent.ORDER_STATE_EVENT, JSON.toJSONString(orderStateEvent));
        LOGGER.info("send order : " + order.getId());
    }

    private void completedOrder(Long orderId) {
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.COMPLETED.getCode());
            orderRepository.updateOrder(order);
            LOGGER.info("订单成功结算-completedOrder: " + order);
        }
    }

    private void failOrder(Long orderId, String message) {
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.FAILURE.getCode());
            orderRepository.updateOrder(order);
            LOGGER.info(String.format("订单失败(%s)-failOrder: %s", message, order));
        }
    }
}
