package pers.cocoadel.learning.redis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class JedisOperator implements RedisOperator {

    private final JedisPool jedisPool;

    private final Logger logger = LoggerFactory.getLogger(JedisOperator.class);

    private final static Long ACQUIRE_JEDIS_TIME_OUT = 100L;

    private final String LOCK_SCRIPT;

    private final String UN_LOCK_SCRIPT;

    private final String REENTRANT_LOCK_SCRIPT;

    private final String UN_REENTRANT_LOCK_SCRIPT;

    private final String INCRBY_TOP_LIMIT_SCRIPT;

    private final String DECRBY_TOP_LIMIT_SCRIPT;

    public JedisOperator(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        LOCK_SCRIPT = readScript("/META-INF/redis-scripts/try-lock");
        UN_LOCK_SCRIPT = readScript("/META-INF/redis-scripts/un-lock");
        REENTRANT_LOCK_SCRIPT = readScript("/META-INF/redis-scripts/try-reentrant-lock");
        UN_REENTRANT_LOCK_SCRIPT = readScript("/META-INF/redis-scripts/try-reentrant-lock");
        INCRBY_TOP_LIMIT_SCRIPT = readScript("/META-INF/redis-scripts/incrby-top-limit");
        DECRBY_TOP_LIMIT_SCRIPT = readScript("/META-INF/redis-scripts/decrby-top-limit");

    }

    /**
     * 尝试获取分布式锁
     */
    public Boolean tryLock(String key, String keyId, Long exTime, TimeUnit timeUnit) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                Object eval = jedis.eval(REENTRANT_LOCK_SCRIPT, Collections.singletonList(key),
                        Arrays.asList(keyId, timeUnit.toSeconds(exTime) + ""));
                return eval != null;
            } finally {
                jedis.close();
            }
        }
        return false;
    }

    @Override
    public Boolean tryLock(final String key, final String keyId, final Long exTime, final TimeUnit exTimeUnit,
                           final Long tryTimeOut, final TimeUnit tryTimeOutUnit) {
        return tryLock(key, keyId, exTimeUnit.toMillis(exTime), tryTimeOutUnit.toMillis(tryTimeOut));
    }

    @Override
    public Boolean tryLock(final String key, final String keyId, final Long exTimeMills, final Long tryTimeOutMills) {
        final AtomicInteger count = new AtomicInteger();
        Boolean result = tryApply(o -> {
            if (!tryLock(key, keyId, exTimeMills, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("try lock failed " + count.incrementAndGet());
            }
            return true;
        }, null, tryTimeOutMills);
        return result != null && result;
    }

    /**
     * 释放分布式锁
     * 如果key和value对应，则尝试删除key，并且返回del操作结果
     * 如果key和value不匹配，说明锁的值已经被改，则返回fasle
     */
    public Boolean releaseLock(String key, String keyId) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                Object result = jedis.eval(UN_LOCK_SCRIPT, Collections.singletonList(key), Collections.singletonList(keyId));
                return result != null && "1".equals(result.toString());
            } finally {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 自增
     */
    @Override
    public Long incr(String key) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return jedis.incr(key);
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 增加，但是最大值不能超过topLimit。
     * 如果增加后的值>topLimit，则不操作，返回空值
     */
    @Override
    public Long incrBy(String key, Long increment, Long topLimit) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                Object eval = jedis.eval(INCRBY_TOP_LIMIT_SCRIPT, Collections.singletonList(key),
                        Arrays.asList(increment.toString(), topLimit.toString()));
                if (eval != null) {
                    return Long.parseLong(eval.toString());
                }
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 自减
     */
    @Override
    public Long decr(String key) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return jedis.decr(key);
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 递减1，最小不超过lowerLmit
     * 如果当前值已经是最小值，则不操作，返回空值
     */
    @Override
    public Long decrBy(String key, Long decrement, Long lowerLimit) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                Object res = jedis.eval(DECRBY_TOP_LIMIT_SCRIPT, Collections.singletonList(key),
                        Arrays.asList(decrement.toString(), lowerLimit.toString()));
                if (res != null) {
                    return Long.valueOf(res.toString());
                }
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public Boolean setNx(String key, String value) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return jedis.setnx(key, value) == 1;
            }finally {
                jedis.close();
            }
        }
        return false;
    }

    @Override
    public Boolean set(String key, String value) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return "OK".equals(jedis.set(key, value));
            }finally {
                jedis.close();
            }
        }
        return false;
    }

    @Override
    public String get(String key) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return jedis.get(key);
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 订阅
     */
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        Jedis jedis = acquireJedis();
        if (jedis == null) {
            throw new NoSuchElementException("acquire jedis failure!");
        }
        try {
            jedis.subscribe(jedisPubSub, channels);
        } finally {
            jedis.close();
        }
    }

    public boolean publish(String channel, String message) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                return jedis.publish(channel, message) == 1;
            } finally {
                jedis.close();
            }
        }
        return false;
    }

    private Jedis acquireJedis() {
        return tryApply(o -> {
            Jedis jedis = jedisPool.getResource();
            if (jedis == null) {
                throw new NullPointerException("jedis is null");
            }
            return jedis;
        },null,ACQUIRE_JEDIS_TIME_OUT);
    }

    private <T,R> R tryApply(Function<T,R> function,T t, long timeOut){
        long endTime = System.currentTimeMillis() + timeOut;
        do{
            try {
                return function.apply(t);
            } catch (Exception e) {
                logger.trace(e.getMessage());
            }
        }while (System.currentTimeMillis() < endTime);
        return null;
    }

    private static String readScript(String scriptPath) {
        String path = JedisOperator.class.getResource(scriptPath).getPath();
        try (FileInputStream fis = new FileInputStream(path);
             Scanner scanner = new Scanner(new InputStreamReader(fis, StandardCharsets.UTF_8));
        ){
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()){
                sb.append(scanner.next()).append("\n");
            }
            if(sb.length() > 0){
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }catch (Exception e){
            throw new RuntimeException("JedisOperator init script error: " + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        String scripts = readScript("/META-INF/redis-scripts/try-lock");
//        System.out.println(scripts);
//    }
}
