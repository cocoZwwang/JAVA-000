package pers.cocoadel.learning.mysql.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pers.cocoadel.learning.mysql.domain.OrderMonthSales;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class OrderMonthSalesDaoImpl implements OrderMonthSalesDao{
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "insert into orders_month_total_sales (month_time,total_sales) values (?,?)";


    @Autowired
    public OrderMonthSalesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void batchSave(List<OrderMonthSales> list) {
        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                OrderMonthSales orderMonthSales = list.get(i);
                ps.setDate(1,new java.sql.Date(orderMonthSales.getDate().getTime()));
                ps.setLong(2,orderMonthSales.getSales());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    @Override
    public List<OrderMonthSales> selectList(Date startTime, Date endTime) {
        String selectSql = String.format("select * from orders_month_total_sales where month_time between '%s' and '%s'",
                timeToString(startTime),timeToString(endTime));
        return jdbcTemplate.query(selectSql,(resultSet,i) ->{
            OrderMonthSales monthSales = new OrderMonthSales();

            monthSales.setDate(new Date(resultSet.getDate(1).getTime()));
            monthSales.setSales(resultSet.getLong(2));

            return monthSales;
        });
    }


    @Override
    public void statOrders(Date startTime, Date endTime) {
        String sql = "select DATE_FORMAT(o.create_time,'%Y-%m') as month_time,\n" +
                "ROUND(sum(p.price*o.product_amount) / 100,2) as sales\n" +
                "from\n" +
                "orders as o\n" +
                "inner join \n" +
                "products as p\n" +
                "on\n" +
                "o.product_id = p.id\n" +
                String.format("where o.create_time between '%s' and '%s'\n",timeToString(startTime),timeToString(endTime))+
                "group by \n" +
                "month_time";
        List<OrderMonthSales> orderMonthSales = jdbcTemplate.query(sql,(resultSet,i) ->{
            OrderMonthSales monthSales = new OrderMonthSales();
            String s = resultSet.getString(1) + "-01 00:00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                monthSales.setDate(sdf.parse(s));
                monthSales.setSales(resultSet.getLong(2));
                return monthSales;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        });

        batchSave(orderMonthSales);
    }

    private String timeToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }
}
