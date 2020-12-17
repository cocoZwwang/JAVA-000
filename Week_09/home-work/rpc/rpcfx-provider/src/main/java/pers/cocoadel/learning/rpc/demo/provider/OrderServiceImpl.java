package pers.cocoadel.learning.rpc.demo.provider;


import pers.cocoade.learning.rpc.api.Order;
import pers.cocoade.learning.rpc.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
