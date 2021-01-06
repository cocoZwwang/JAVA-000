package per.cocoadel.learning.product;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cocoadel.learning.order.Order;
import per.cocoadel.learning.order.OrderRepository;
import per.cocoadel.learning.order.OrderState;
import per.cocoadel.learning.subscrib.DeliverProductEvent;
import per.cocoadel.learning.subscrib.EventType;
import per.cocoadel.learning.subscrib.OrderStateEvent;
import pers.cocoadel.learning.redis.RedisOperator;
import redis.clients.jedis.JedisPubSub;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final RedisOperator redisOperator;

    private final OrderRepository orderRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository, RedisOperator redisOperator, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.redisOperator = redisOperator;
        this.orderRepository = orderRepository;
        this.redisOperator.subscribe(jedisPubSub, EventType.OrderEvent.ORDER_STATE_EVENT);
    }

    private final JedisPubSub jedisPubSub = new JedisPubSub() {
        @Override
        public void onMessage(String channel, String message) {
            LOGGER.info(String.format("receive message from channel(%s): %s",channel,message));
            if(EventType.OrderEvent.ORDER_STATE_EVENT.equals(channel)){
                OrderStateEvent orderStateEvent = JSON.parseObject(message,OrderStateEvent.class);
                updateProductStock(orderStateEvent.getOrderId());
            }
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            super.onSubscribe(channel, subscribedChannels);
        }
    };

    private void updateProductStock(Long orderId) {
        Order order = orderRepository.getOrder(orderId);
        //如果是支付完成，则走出库流程
        if(order != null && order.getState() == OrderState.PAYED.getCode()){
            Product product = productRepository.getProduct(order.getProductId());
            DeliverProductEvent event = new DeliverProductEvent();
            event.setOrderId(orderId);
            if(product.getStock() > order.getProductAmount()){
                productRepository.updateProductStock(product.getId(),
                        product.getStock() - order.getProductAmount());
                event.setMessage("OK");
                LOGGER.info("updateProductStock: " + order);
            }else{
                event.setMessage("stock of product is not enough");
                LOGGER.info("product is not enough: " + order);
            }
            redisOperator.publish(EventType.ProductEvent.DELIVER_PRODUCT_EVENT, JSON.toJSONString(event));
        }
    }
}
