package pers.cocoadel.learning.redis;

public class RedisIncrLimitException extends RuntimeException {
    public RedisIncrLimitException(Long topLimit) {
        super(String.format("incr value failure! it's already the max(%s)", topLimit));
    }
}
