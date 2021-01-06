package per.cocoadel.learning.demo.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pers.cocoadel.learning.redis.core.RedisOperator;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {
    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    private final RedisOperator redisOperator;

    @Autowired
    public ProductRepository(RedisOperator redisOperator){
        this.redisOperator = redisOperator;
    }

    @PostConstruct
    public void init(){
        List<Product> products = createProducts();
        for (Product product : products) {
            productMap.put(product.getId(), product);
            redisOperator.set(getProductStockKey(product.getId()),product.getStock() + "");
        }
    }

    /**
     * Redis分布式计数器，模拟减库存
     */
    public Boolean decrByProductStock(Long productId,Long decrement){
        Product product = productMap.get(productId);
        if(product != null){
            //如果减库存成功，返回减少后的库存
            //如果减少后的库存<0,则不执行减库存操作,返回null
            return redisOperator.decrBy(getProductStockKey(product.getId()),decrement,0L) != null;
        }
        return true;
    }

    public Product getProduct(Long productId) {
        Product product = productMap.get(productId);
        if(product != null) {
            //从redis中拿出库存
            String res = redisOperator.get(getProductStockKey(productId));
            product.setStock(StringUtils.isEmpty(res) ? 0 : Integer.parseInt(res));
        }
        return product;
    }

    private String getProductStockKey(Long productId){
        return String.format("product:id:%s:stock",productId);
    }

    private List<Product> createProducts(){
        List<Product> products = new LinkedList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("IPhone");
        product.setStock(10);
        product.setPrice(10000 * 100L);
        products.add(product);

        product = new Product();
        product.setId(2L);
        product.setName("Mac Book");
        product.setStock(0);
        product.setPrice(10000 * 100L);
        products.add(product);
        return products;
    }


}
