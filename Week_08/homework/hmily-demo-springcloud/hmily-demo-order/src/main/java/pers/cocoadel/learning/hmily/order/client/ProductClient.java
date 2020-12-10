package pers.cocoadel.learning.hmily.order.client;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.cocoadel.learning.hmily.order.domain.Order;
import pers.cocoadel.learning.hmily.order.domain.Product;

@FeignClient(value = "CLOUD-PRODUCT")
public interface ProductClient {

    @Hmily
    @PostMapping("products/update_stock_by_order")
    void updateProduct(@RequestBody Order order);

    @GetMapping("products/{productId}")
    Product findById(@PathVariable("productId") Long productId);
}
