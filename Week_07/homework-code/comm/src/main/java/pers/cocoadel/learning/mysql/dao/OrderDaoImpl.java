package pers.cocoadel.learning.mysql.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.cocoadel.learning.mysql.domain.Order;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class OrderDaoImpl implements OrderDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    @Override
    public void batchSave(List<Order> orders) {
        if(CollectionUtils.isEmpty(orders)){
            return;
        }
        String sql = "insert into orders (id,user_id,product_id,product_amount,state,create_time,update_time) " +
                "values(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Order order = orders.get(i);
                ps.setLong(1, order.getId());
                ps.setLong(2,order.getUserId());
                ps.setLong(3,order.getProductId());
                ps.setInt(4,order.getProductAmount());
                ps.setInt(5, order.getState());
                ps.setDate(6,new Date(order.getCreateTime().getTime()));
                ps.setDate(7,new Date(order.getUpdateTime().getTime()));
            }

            @Override
            public int getBatchSize() {
                return orders.size();
            }
        });
    }
}
