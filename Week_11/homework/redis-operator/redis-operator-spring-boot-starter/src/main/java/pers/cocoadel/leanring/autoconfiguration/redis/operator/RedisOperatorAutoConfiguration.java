package pers.cocoadel.leanring.autoconfiguration.redis.operator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.cocoadel.learning.redis.core.JedisOperator;
import pers.cocoadel.learning.redis.core.RedisOperator;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ConditionalOnClass(name = "pers.cocoadel.learning.redis.core.RedisOperator")
@Configuration
public class RedisOperatorAutoConfiguration {

    @Bean
    @ConfigurationProperties("redis")
    public JedisPoolConfig jedisPoolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(16);
        jedisPoolConfig.setMaxIdle(16);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setBlockWhenExhausted(true);
        return jedisPoolConfig;
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool(@Value("${redis.host}") String host) {
        return new JedisPool(jedisPoolConfig(), host);
    }

    @Bean
    public RedisOperator redisOperator(JedisPool jedisPool) {
        return new JedisOperator(jedisPool);
    }
}
