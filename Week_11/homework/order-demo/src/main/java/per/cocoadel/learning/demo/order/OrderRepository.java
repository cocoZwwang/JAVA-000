package per.cocoadel.learning.demo.order;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {
    private final Map<Long,Order> orderMap = new ConcurrentHashMap<>();

    public void addOrder(Order order){
        if (orderMap.containsKey(order.getId())) {
            throw new RuntimeException(String.format("order id %s is already exists",order.getId()));
        }
        orderMap.put(order.getId(), order);
    }

    public Order getOrder(Long orderId){
        return orderMap.get(orderId);
    }

    public void updateOrder(Order order){
        if (!orderMap.containsKey(order.getId())) {
            throw new NoSuchElementException(String.format("order id %s is not exists", order.getId()));
        }
        orderMap.put(order.getId(), order);
    }
}
