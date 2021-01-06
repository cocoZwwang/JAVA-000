package per.cocoadel.learning.product;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {
    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    public ProductRepository(){
        List<Product> products = createProducts();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }
    }

    public void updateProductStock(Long productId,int stock){
        Product product = productMap.get(productId);
        if(product != null){
            product.setStock(stock);
        }
    }

    public Product getProduct(Long productId) {
        return productMap.get(productId);
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
        product.setId(1L);
        product.setName("Mac Book");
        product.setStock(0);
        product.setPrice(10000 * 100L);
        products.add(product);
        return products;
    }


}
