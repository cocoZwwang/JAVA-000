package pers.cocoadel.learning.hmily.product.service;

import pers.cocoadel.learning.hmily.product.domain.Order;
import pers.cocoadel.learning.hmily.product.domain.Product;

import java.util.Collection;

public interface ProductService {
    void updateProductStock(Order order);

    void batchSave(Collection<Product> products);

    Product findById(Long productId);

    Product showProduct(Long productId);

    void deleteAll();
}
