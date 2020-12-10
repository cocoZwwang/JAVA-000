package pers.cocoadel.learning.hmily.order.dao;

import pers.cocoadel.learning.hmily.order.domain.Order;

import java.util.Collection;

public interface OrderDao {
    void batchSave(Collection<Order> orders);

    Integer updateState(Order order);

    Integer deleteAll();

    Order selectOne(Long orderId);
}
