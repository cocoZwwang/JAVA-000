package pers.cocoadel.learning.subdatabase.dao;


import pers.cocoadel.learning.subdatabase.domain.Order;
import java.util.Collection;
import java.util.List;

public interface OrderDao {
    void batchSave(List<Order> orders);

    Integer updateState(Order order);

    List<Order> selectByUserId(Long userId);

    Integer deleteByIdsAndUserId(Collection<Long> ids, Long userId);

    Integer deleteAll();
}
