package per.cocoadel.learning.order;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cocoadel.learning.subscrib.DeliverProductEvent;
import per.cocoadel.learning.subscrib.EventType;
import per.cocoadel.learning.subscrib.OrderStateEvent;
import pers.cocoadel.learning.redis.RedisOperator;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final RedisOperator redisOperator;

    private final AtomicLong idCreator = new AtomicLong(0);

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, RedisOperator redisOperator) {
        this.orderRepository = orderRepository;
        this.redisOperator = redisOperator;
        //订阅仓库出货通知
        redisOperator.subscribe(deliverProductSub, EventType.ProductEvent.DELIVER_PRODUCT_EVENT);
    }

    public void placeOrder() {
        //生成订单
        Order order = new Order();
        order.setId(idCreator.incrementAndGet());
        order.setProductId(1L);
        order.setProductAmount(1);
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

    private void completedOrder(Long orderId){
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.COMPLETED.getCode());
            orderRepository.updateOrder(order);
            LOGGER.info("completedOrder: " + order);
        }
    }

    private void failOrder(Long orderId){
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.FAILURE.getCode());
            orderRepository.updateOrder(order);
            LOGGER.info("failOrder: " + order);
        }
    }


    private final JedisPubSub deliverProductSub = new JedisPubSub() {
        @Override
        public void onMessage(String channel, String message) {
            LOGGER.info(String.format("receive message from channel(%s): %s",channel,message));
            if (EventType.ProductEvent.DELIVER_PRODUCT_EVENT.equals(channel)) {
                DeliverProductEvent deliverProductEvent = JSON.parseObject(message, DeliverProductEvent.class);
                if("OK".equals(deliverProductEvent.getMessage())){
                    completedOrder(deliverProductEvent.getOrderId());
                }else{
                    failOrder(deliverProductEvent.getOrderId());
                }

            }
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            super.onSubscribe(channel, subscribedChannels);
        }
    };


}
