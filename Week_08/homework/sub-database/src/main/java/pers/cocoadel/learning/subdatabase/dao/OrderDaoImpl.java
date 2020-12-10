package pers.cocoadel.learning.subdatabase.dao;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pers.cocoadel.learning.subdatabase.domain.Order;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
public class OrderDaoImpl implements OrderDao{

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }


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
    public List<Order> selectByUserId(Long userId) {
        String sql = "select * from orders where user_id = " + userId;
        return jdbcTemplate.query(sql, (resultSet,i) -> {
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

    @Override
    public Integer deleteByIdsAndUserId(Collection<Long> ids, Long userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",ids);
        map.put("userId",userId);
        String sql = "delete from orders where user_id =:userId and id in (:id)";
        return namedParameterJdbcTemplate.update(sql,map);
    }

    @Override
    public Integer deleteAll() {
        return jdbcTemplate.update("delete from orders");
    }
}
