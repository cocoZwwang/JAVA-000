package per.cocoadel.learning.order;

import com.alibaba.fastjson.JSON;
import per.cocoadel.learning.product.DeliverProductMessage;
import pers.cocoadel.learning.redis.RedisOperator;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    private final OrderRepository orderRepository;

    private final RedisOperator redisOperator;

    private final AtomicLong idCreator = new AtomicLong(0);

    private final String DELIVER_PRODUCT_CHANNEL = "deliver_product_channel";

    private final String ORDER_STATE_CHANNEL = "order_state_channel";

    public OrderService(OrderRepository orderRepository, RedisOperator redisOperator) {
        this.orderRepository = orderRepository;
        this.redisOperator = redisOperator;
        //订阅仓库出货通知
        redisOperator.subscribe(deliverProductSub,DELIVER_PRODUCT_CHANNEL);
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
        //发生订单生成消息
        OrderStateEvent orderStateEvent = new OrderStateEvent();
        orderStateEvent.setOrderId(order.getId());
        orderStateEvent.setOrderState(order.getState());
        redisOperator.publish(ORDER_STATE_CHANNEL, JSON.toJSONString(orderStateEvent));
    }

    private void completedOrder(Long orderId){
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.COMPLETED.getCode());
            orderRepository.updateOrder(order);
        }
    }

    private void failOrder(Long orderId){
        Order order = orderRepository.getOrder(orderId);
        if (order != null) {
            order.setState(OrderState.FAILURE.getCode());
            orderRepository.updateOrder(order);
        }
    }


    private final JedisPubSub deliverProductSub = new JedisPubSub() {
        @Override
        public void onMessage(String channel, String message) {
            if (DELIVER_PRODUCT_CHANNEL.equals(channel)) {
                DeliverProductMessage deliverProductMessage = JSON.parseObject(message,DeliverProductMessage.class);
                if("OK".equals(deliverProductMessage.getMessage())){
                    completedOrder(deliverProductMessage.getOrderId());
                }else{
                    failOrder(deliverProductMessage.getOrderId());
                }

            }
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            super.onSubscribe(channel, subscribedChannels);
        }
    };


}
