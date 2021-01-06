package pers.cocoadel.learning.redis;

import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.TimeUnit;

public interface RedisOperator {

    Boolean tryLock(String key, String keyId, Long timeOut, TimeUnit timeUnit);

    Boolean releaseLock(String key,String keyId);

    Long incr(String key);

    Long incr(String key,Long topLimit);

    Long decr(String key);

    Long decr(String key,Long lowerLimit);

    Boolean setNx(String key,String value);

    String get(String key);

    void subscribe(JedisPubSub jedisPubSub, String... channels);

    boolean publish(String channel, String message);


}
