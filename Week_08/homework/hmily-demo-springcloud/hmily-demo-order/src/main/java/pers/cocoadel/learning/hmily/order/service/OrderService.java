package pers.cocoadel.learning.hmily.order.service;

import pers.cocoadel.learning.hmily.order.domain.Order;

public interface OrderService {

    Order showOrder(Long orderId);

    void orderPay(Order order);

    void orderPayAndThrowException(Order order);


}
