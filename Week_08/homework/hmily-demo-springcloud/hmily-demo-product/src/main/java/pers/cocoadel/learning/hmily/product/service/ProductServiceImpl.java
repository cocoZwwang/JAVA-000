package pers.cocoadel.learning.hmily.product.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.cocoadel.learning.hmily.product.dao.ProductDao;
import pers.cocoadel.learning.hmily.product.domain.Order;
import pers.cocoadel.learning.hmily.product.domain.Product;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductDao productDao;

    private final Map<String,Order> map = new ConcurrentHashMap<>();

    @Autowired
    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @HmilyTCC(confirmMethod = "updateProductConfrim", cancelMethod = "updateProductCancel")
    @Transactional
    @Override
    public void updateProductStock(Order order) {
        Product product = productDao.selectOne(order.getProductId());
        //TODO 存在多事务增量问题，之后再加个乐观锁去解决。
        product.setStock(product.getStock() - order.getProductAmount());
        productDao.update(product);
        //保存更新前的Order数据，用于cancel
        //最好使用持久化存储，这里demo使用内存
        map.put(order.getId().toString(),order);
        log.info("updateProduct try!");
    }

    @Transactional
    public void updateProductConfrim(Order order){
        //成功后删除key
        map.remove(order.getId().toString());
        log.info("updateProduct Confrim!");
    }


    @Transactional
    public void updateProductCancel(Order order){
        //orderId作为key存在表示之前已经Try并且这是第一次Cancel
        if(map.containsKey(order.getId().toString())){
            Product product = productDao.selectOne(order.getProductId());
            //TODO 存在多线程增量问题，之后再加个乐观锁去解决。
            product.setStock(product.getStock() + order.getProductAmount());
            productDao.update(product);
            map.remove(order.getId().toString());
        }
        log.info("updateProduct Cancel!");
    }


    @Transactional
    @Override
    public void batchSave(Collection<Product> products) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        productDao.batchSave(products);
    }

    @Transactional(readOnly = true)
    @Override
    public Product findById(Long productId) {
        return productDao.selectOne(productId);
    }

    @Transactional(readOnly = true)
    @Override
    public Product showProduct(Long productId) {
        Product product = productDao.selectOne(productId);
        log.info(product.toString());
        return product;
    }

    @Transactional
    @Override
    public void deleteAll() {
        productDao.deleteAll();
    }
}
