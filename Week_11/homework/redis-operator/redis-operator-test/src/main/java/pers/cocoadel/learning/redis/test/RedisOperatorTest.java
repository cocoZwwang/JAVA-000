package pers.cocoadel.learning.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pers.cocoadel.learning.redis.core.RedisOperator;
import redis.clients.jedis.JedisPubSub;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RedisOperatorTest implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(RedisOperatorTest.class);

    @Autowired
    RedisOperator redisOperator;

    int num = 0;

    public static void main(String[] args) {
        SpringApplication.run(RedisOperatorTest.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
//            //测试分布式锁
//            testDistributedLock();
//            //测试分布式计数器
//            testCounter();
            //测试发布订阅
            testPubSub();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试分布式锁
     */
    private void testDistributedLock() {
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                String keyId = UUID.randomUUID().toString().replace("-", "");
                while (true) {
                    if (redisOperator.tryLock("dKey", keyId, 100L, TimeUnit.SECONDS)) {
                        //模拟业务耗时
                        try {
                            Thread.sleep((long) (Math.random() * 1000L));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        logger.info("num: " + (++num));
                        redisOperator.releaseLock("dKey", keyId);
                        break;
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * 测试计数器
     */
    private void testCounter() throws InterruptedException {
        CountDownLatch countDownLatch1 = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                Long res = redisOperator.incrBy("count-test",10L, 80L);
                logger.info(res == null ? "nil" : res.toString());
                countDownLatch1.countDown();
            });
            thread.start();
        }
        countDownLatch1.await();
        //输出80
        logger.info(redisOperator.get("count-test"));

        CountDownLatch countDownLatch2 = new CountDownLatch(100);
        for (int i = 0; i < 101; i++) {
            Thread thread = new Thread(() -> {
                Long res = redisOperator.decrBy("count-test",10L, 0L);
                logger.info(res == null ? "nil" : res.toString());
                countDownLatch2.countDown();
            });
            thread.start();
        }
        countDownLatch2.await();
        //输出0
        logger.info(redisOperator.get("count-test"));
    }


    private void testPubSub() {
        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                logger.info(String.format("receive the message from channel[%s]: %s",channel,message));
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                logger.info(String.format("onSubscribe channel[%s]: %s",channel,subscribedChannels));
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                logger.info(String.format("onUnsubscribe channel[%s]: %s",channel,subscribedChannels));
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            redisOperator.subscribe(jedisPubSub,"test-channel");
        });
        for (int i = 0; i < 10; i++){
            redisOperator.publish("test-channel","message-" +i);
        }
    }
}
