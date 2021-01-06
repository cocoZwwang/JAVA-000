package per.cocoadel.learning.demo.product;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cocoadel.learning.demo.order.Order;
import per.cocoadel.learning.demo.order.OrderRepository;
import per.cocoadel.learning.demo.order.OrderState;
import per.cocoadel.learning.demo.subscrib.DeliverProductEvent;
import per.cocoadel.learning.demo.subscrib.EventType;
import per.cocoadel.learning.demo.subscrib.OrderStateEvent;
import pers.cocoadel.learning.redis.core.RedisOperator;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final RedisOperator redisOperator;

    private final OrderRepository orderRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository, RedisOperator redisOperator, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.redisOperator = redisOperator;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        //订阅订单事件
        executorService.submit(() ->
                this.redisOperator.subscribe(orderStateSub, EventType.OrderEvent.ORDER_STATE_EVENT));
    }

    private void updateProductStock(Long orderId) {
        Order order = orderRepository.getOrder(orderId);
        //如果是支付完成，则走出库流程
        if (order != null && order.getState() == OrderState.PAYED.getCode()) {
            Product product = productRepository.getProduct(order.getProductId());
            DeliverProductEvent event = new DeliverProductEvent();
            event.setOrderId(orderId);
            Long decrement = order.getProductAmount() == null ? 0L : order.getProductAmount();
            if (productRepository.decrByProductStock(product.getId(), decrement)) {
                //减库存成功，
                event.setMessage("OK");
                LOGGER.info("updateProductStock: " + order);
            } else {
                //减库存失败
                event.setMessage("stock of product is not enough");
                LOGGER.info("product is not enough: " + order);
            }
            //发射事件订阅到redis
            redisOperator.publish(EventType.ProductEvent.DELIVER_PRODUCT_EVENT, JSON.toJSONString(event));
        }
    }

    private final JedisPubSub orderStateSub = new JedisPubSub() {
        @Override
        public void onMessage(String channel, String message) {
            LOGGER.info(String.format("receive message from channel(%s): %s", channel, message));
            //如果是订单事件，则处理
            if (EventType.OrderEvent.ORDER_STATE_EVENT.equals(channel)) {
                OrderStateEvent orderStateEvent = JSON.parseObject(message, OrderStateEvent.class);
                updateProductStock(orderStateEvent.getOrderId());
            }
        }
        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            super.onSubscribe(channel, subscribedChannels);
        }
    };
}
