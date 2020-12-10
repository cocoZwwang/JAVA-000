package pers.cocoadel.learning.hmily.product.dao;

import pers.cocoadel.learning.hmily.product.domain.Product;

import java.util.Collection;
import java.util.List;

public interface ProductDao {
    void batchSave(Collection<Product> products);

    List<Product> selectList();

    Product selectOne(Long productId);

    Integer update(Product product);

    void deleteById(Long productId);

    void deleteAll();
}
