package pers.cocoadel.learning.hmily.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.cocoadel.learning.hmily.product.domain.Order;
import pers.cocoadel.learning.hmily.product.domain.Product;
import pers.cocoadel.learning.hmily.product.service.ProductServiceImpl;

@RestController
public class ProductController {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }


    @GetMapping("products/{productId}")
    public Product findById(@PathVariable Long productId){
        return productService.findById(productId);
    }

    @PostMapping("products/update_stock_by_order")
    public void updateProductStock(@RequestBody Order order){
        productService.updateProductStock(order);
    }
}
