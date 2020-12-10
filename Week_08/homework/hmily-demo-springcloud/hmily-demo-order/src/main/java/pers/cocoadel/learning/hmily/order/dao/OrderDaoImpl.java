package pers.cocoadel.learning.hmily.order.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import pers.cocoadel.learning.hmily.order.domain.Order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void batchSave(Collection<Order> orders) {
        if(CollectionUtils.isEmpty(orders)){
            return;
        }
        List<Order> list = new ArrayList<>(orders);
        String sql = "insert into orders (id,user_id,product_id,product_amount,state,create_time,update_time) " +
                "values(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Order order = list.get(i);
                ps.setLong(1, order.getId());
                ps.setLong(2,order.getUserId());
                ps.setLong(3,order.getProductId());
                ps.setInt(4,order.getProductAmount());
                ps.setInt(5, order.getState());
                ps.setDate(6,new java.sql.Date(order.getCreateTime().getTime()));
                ps.setDate(7,new java.sql.Date(order.getUpdateTime().getTime()));
            }

            @Override
            public int getBatchSize() {
                return orders.size();
            }
        });
    }

    @Override
    public Integer updateState(Order order) {
        String sql  = "update orders set state = ? where id = ? and user_id = ?";
        return jdbcTemplate.update(sql,order.getState(),order.getId(),order.getUserId());
    }

    @Override
    public Integer deleteAll() {
        return jdbcTemplate.update("delete from orders");
    }

    @Override
    public Order selectOne(Long orderId) {
        String sql = "select * from orders where id = " + orderId;
        return jdbcTemplate.queryForObject(sql, (resultSet,i) -> {
            Order order = new Order();
            order.setId(resultSet.getLong(1));
            order.setUserId(resultSet.getLong(2));
            order.setProductId(resultSet.getLong(3));
            order.setProductAmount(resultSet.getInt(4));
            order.setState(resultSet.getInt(5));
            order.setCreateTime(new Date(resultSet.getDate(6).getTime()));
            order.setUpdateTime(new Date(resultSet.getDate(7).getTime()));
            return order;
        });
    }
}
