package pers.cocoadel.learning.mysql.dao;

import pers.cocoadel.learning.mysql.domain.Order;

import java.util.List;

public interface OrderDao {
    void batchSave(List<Order> orders);
}
