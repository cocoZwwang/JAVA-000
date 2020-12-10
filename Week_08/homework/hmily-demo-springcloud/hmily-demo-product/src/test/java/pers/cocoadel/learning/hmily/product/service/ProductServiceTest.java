package pers.cocoadel.learning.hmily.product.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void findById() {
        productService.showProduct(1L);
    }

    @Test
    public void updateProduct() {
//        Product product = productService.showProduct(1L);
//        product.setStock(product.getStock() - 1);
//        productService.updateProduct(product);
//        productService.showProduct(product.getId());
    }
}