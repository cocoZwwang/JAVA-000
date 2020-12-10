package pers.cocoadel.learning.hmily.order.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.cocoadel.learning.hmily.order.client.ProductClient;
import pers.cocoadel.learning.hmily.order.dao.OrderDao;
import pers.cocoadel.learning.hmily.order.domain.Order;
import pers.cocoadel.learning.hmily.order.enums.OrderState;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;

    private final ProductClient productClient;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao, ProductClient productClient) {
        this.orderDao = orderDao;
        this.productClient = productClient;
    }

    @HmilyTCC(confirmMethod = "orderPayConfirm", cancelMethod = "orderPayCancel")
    @Transactional
    @Override
    public void orderPay(Order order) {
        //更新商品库存
        productClient.updateProduct(order);
    }

    @HmilyTCC(confirmMethod = "orderPayConfirm", cancelMethod = "orderPayCancel")
    @Transactional
    @Override
    public void orderPayAndThrowException(Order order) {
        //更新商品库存
        productClient.updateProduct(order);
        throw new RuntimeException("服务器异常！");
    }

    public void orderPayConfirm(Order order) {
        order.setState(OrderState.SUCCESS.getState());
        orderDao.updateState(order);
        log.info("order[id:{}] orderPayConfirm!",order.getId());
    }

    public void orderPayCancel(Order order) {
        order.setState(OrderState.Filed.getState());
        orderDao.updateState(order);
        log.info("order[id:{}] orderPayCancel!",order.getId());
    }

    @Transactional(readOnly = true)
    public Order showOrder(Long orderId) {
        Order order = orderDao.selectOne(orderId);
        log.info(order.toString());
        return order;
    }
}
