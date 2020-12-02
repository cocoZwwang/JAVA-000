package pers.cocoadel.learning.mysql.dao;

import pers.cocoadel.learning.mysql.domain.OrderMonthSales;

import java.util.Date;
import java.util.List;

public interface OrderMonthSalesDao {
    void batchSave(List<OrderMonthSales> list);

    List<OrderMonthSales> selectList(Date startTime,Date endTime);

    void statOrders(Date startTime,Date endTime);
}
