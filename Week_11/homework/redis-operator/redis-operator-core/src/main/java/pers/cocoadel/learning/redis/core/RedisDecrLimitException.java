package pers.cocoadel.learning.redis.core;

public class RedisDecrLimitException extends RuntimeException {
    public RedisDecrLimitException(Long lowerLimit){
        super(String.format("incr value failure! it's already the max(%s)", lowerLimit));
    }
}
