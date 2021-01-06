package pers.cocoadel.learning.redis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class JedisOperator implements RedisOperator {

    private final JedisPool jedisPool;

    private final Logger logger = LoggerFactory.getLogger(JedisOperator.class);

    private final static Long ACQUIRE_JEDIS_TIME_OUT = 100L;

    private static final String LOCK_SCRIPT = "if (redis.call('setnx',KEYS[1],ARGV[1]) < 1) " +
            "then return 0; end;" +
            "redis.call('expire',KEYS[1],tonumber(ARGV[2])); return 1;";

    private static final String UN_LOCK_SCRIPT = "if (redis.call('get', KEYS[1]) == ARGV[1])\n" +
            "then return redis.call('del', KEYS[1])\n" +
            "else return 0\n" +
            "end";

    private static final String INCRBY_TOP_LIMIT_SCRIPT =
            "if (redis.call('exists', KEYS[1]) == 0 or (tonumber(redis.call('get', KEYS[1]) + tonumber(ARGV[1])) <= tonumber(ARGV[2])))\n" +
                    "then return redis.call('incrby', KEYS[1], ARGV[1]);\n" +
                    "else return nil; end;";

    private static final String DECRBY_TOP_LIMIT_SCRIPT =
            "if (redis.call('exists', KEYS[1]) == 0 or (tonumber(redis.call('get', KEYS[1]) - tonumber(ARGV[1])) >= tonumber(ARGV[2])))\n" +
                    "then return redis.call('decrby', KEYS[1], ARGV[1]);\n" +
                    "else return nil; end;";

    public JedisOperator(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 尝试获取分布式锁
     */
    public Boolean tryLock(String key, String keyId, Long timeOut, TimeUnit timeUnit) {
        Jedis jedis = acquireJedis();
        if (jedis != null) {
            try {
                Object eval = jedis.eval(LOCK_SCRIPT, Collections.singletonList(key),
                        Arrays.asList(keyId, timeUnit.toSeconds(timeOut) + ""));
                return eval != null && "1".equals(eval.toString());
            } finally {
                jedis.close();
            }
        }
        return false;
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
        long endTime = System.currentTimeMillis() + ACQUIRE_JEDIS_TIME_OUT;
        do {
            try {
                Jedis jedis = jedisPool.getResource();
                if (jedis != null) {
                    return jedis;
                }
            } catch (JedisExhaustedPoolException e) {
                logger.trace(e.getMessage());
            }
        } while (System.currentTimeMillis() < endTime);
        return null;
    }
}
