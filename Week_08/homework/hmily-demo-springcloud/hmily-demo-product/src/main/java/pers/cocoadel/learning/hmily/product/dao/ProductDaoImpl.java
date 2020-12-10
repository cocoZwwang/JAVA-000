package pers.cocoadel.learning.hmily.product.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pers.cocoadel.learning.hmily.product.domain.Product;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ProductDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public void batchSave(Collection<Product> products) {
        String sql = "insert into products (id,name,class_id,price,stock,create_time,update_time,create_by,update_by) " +
                "values(?,?,?,?,?,?,?,?,?)";
        List<Product> list = new ArrayList<>(products);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Product product = list.get(i);
                ps.setLong(1, product.getId());
                ps.setString(2, product.getName());
                ps.setLong(3, product.getClassId());
                ps.setLong(4, product.getPrice());
                ps.setInt(5, product.getStock());
                ps.setDate(6, new Date(product.getCreateTime().getTime()));
                ps.setDate(7, new Date(product.getUpdateTime().getTime()));
                ps.setLong(8, product.getCreateBy());
                ps.setLong(9, product.getUpdateBy());
            }

            @Override
            public int getBatchSize() {
                return products.size();
            }
        });
    }

    @Override
    public List<Product> selectList() {
        String sql = "select * from products";
        return jdbcTemplate.query(sql, (resultSet, i) -> resultSetToProduct(resultSet));
    }

    @Override
    public Product selectOne(Long productId) {
        String sql = "select * from products where id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{productId},
                (resultSet, i) -> resultSetToProduct(resultSet));
    }

    private Product resultSetToProduct(ResultSet resultSet) throws SQLException {
        final Product product = new Product();
        product.setId(resultSet.getLong(1));
        product.setName(resultSet.getString(2));
        product.setClassId(resultSet.getLong(3));
        product.setPrice(resultSet.getLong(4));
        product.setStock(resultSet.getInt(5));
        product.setCreateTime(new java.util.Date(resultSet.getDate(6).getTime()));
        product.setUpdateTime(new java.util.Date(resultSet.getDate(7).getTime()));
        product.setCreateBy(resultSet.getLong(8));
        product.setUpdateBy(resultSet.getLong(9));
        return product;
    }

    @Override
    public Integer update(Product product) {
        String sql = "update products set stock = ? where id = ?";
        return jdbcTemplate.update(sql,product.getStock(),product.getId());
    }

    @Override
    public void deleteById(Long productId) {
        String sql = "delete from products where id = ?";
        jdbcTemplate.update(sql,productId);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from products");
    }
}
