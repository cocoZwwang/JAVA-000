package pers.cocoadel.learning.subdatabase.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.cocoadel.learning.subdatabase.dao.OrderDao;
import pers.cocoadel.learning.subdatabase.domain.Order;

import java.util.*;

@Slf4j
@Component
public class OrderService {
    @Autowired
    OrderDao orderDao;

    /**
     * 根据userId查询并且打印相关订单信息
     */
    @Transactional(readOnly = true)
    public void showByUserId(long userId) {
        List<Order> orders = orderDao.selectByUserId(userId);
        log.info("userId为{}的用户的order数目:{}",userId,orders.size());
        orders.forEach(order -> log.info(order.toString()));
    }

    /**
     * 查询订单
     */
    @Transactional(readOnly = true)
    public List<Order> selectOrders(long userId) {
        return orderDao.selectByUserId(userId);
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public void update(Order order) {
        orderDao.updateState(order);
    }

    /**
     * 删除
     */
    @Transactional
    public void delete(Collection<Long> ids, Long userId) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        orderDao.deleteByIdsAndUserId(ids, userId);
    }

    @Transactional
    public void deleteAll(){
        orderDao.deleteAll();
    }

    /**
     * 随机生成数据并且插入
     */
    @Transactional
    public void insertOrders(int count) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            List<Order> orders = createOrders(i * 1000000 + 1, count);
            orderDao.batchSave(orders);
        }
        log.info("插入完成,耗时：{}s", (System.currentTimeMillis() - time) / 1000);
    }

    /**
     * 随机生成order数据
     */
    private List<Order> createOrders(int start, int count) {
        List<Order> list = new ArrayList<>();
        for (int i = start; i < start + count; i++) {
            Order order = new Order();
            order.setId((long) i);
            order.setUserId(1 + (long) (Math.random() * 10000));
            order.setProductId(1 + (long) (Math.random() * 2000));
            order.setProductAmount((int) (1 + Math.random() * 10));
            order.setState(0);
            Date date = randDate();
            order.setCreateTime(date);
            order.setUpdateTime(date);

            list.add(order);
        }
        return list;
    }

    /**
     * 在2020年内随机日期
     */
    private Date randDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 0, 1);
        calendar.add(Calendar.DATE, (int) (Math.random() * 365));
        return calendar.getTime();
    }
}
