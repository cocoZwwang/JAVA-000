## 4.（必做）基于 Redis 封装分布式数据操作：

- 在 Java 中实现一个简单的分布式锁；
  - 尝试获取锁：[JedisOperator#tryLock(String , String , Long , TmeUnit)](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/redis-operator/redis-operator-core/src/main/java/pers/cocoadel/learning/redis/core/JedisOperator.java)。
  - 释放锁：[releaseLock(String , String )。](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/redis-operator/redis-operator-core/src/main/java/pers/cocoadel/learning/redis/core/JedisOperator.java)
- 在 Java 中实现一个分布式计数器，模拟减库存。
  - 分布式计数器：[JedisOperator.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/redis-operator/redis-operator-core/src/main/java/pers/cocoadel/learning/redis/core/JedisOperator.java)
    - 自增：JedisOperator#incr(String)
    - 自减：JedisOperator#decr(String)
    - 原子增加increment，但是不能超过指定上限：JedisOperator# incrBy(String, Long, Long)
    - 原子减少decrement，但是不能低于下限：JedisOperator#incrBy(String, Long, Long)
  - 分布式计数器模拟库存计算：[ProductRepository.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/order-demo/src/main/java/per/cocoadel/learning/demo/product/ProductRepository.java)

## 5.（必做）基于 Redis 的 PubSub 实现订单异步处理

- 程序测试入口：[OrderApplication.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/order-demo/src/main/java/per/cocoadel/learning/demo/OrderApplication.java)
- 订单服务：[OrderService.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/order-demo/src/main/java/per/cocoadel/learning/demo/order/OrderService.java)
- 库存服务：[ProductService.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_11/homework/order-demo/src/main/java/per/cocoadel/learning/demo/product/ProductService.java)