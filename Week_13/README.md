# 1、（必做）搭建ActiveMQ服务，基于JMS，写代码分别实现对于queue和topic的消息生产和消费，代码提交到github。

## 基于topic

生产者端代码： [TopicProducerService](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/activeMq-demo/activemq-producer/src/main/java/pers/cocoadel/learning/activemq/producer/TopicProducerService.java)  

消费者端代码： [TopicConsumerService](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/activeMq-demo/activemq-consumer/src/main/java/pers/cocoadel/learning/activemq/consumer/TopicConsumerService.java)

## 基于Queue

生产者端代码： [QueueProducerService](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/activeMq-demo/activemq-producer/src/main/java/pers/cocoadel/learning/activemq/producer/QueueProducerService.java)  

消费者端代码： [QueueConsumerService](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/activeMq-demo/activemq-consumer/src/main/java/pers/cocoadel/learning/activemq/consumer/QueueConsumerService.java)  



#  1.（必做）搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作，将代码提交到 github。

### 配置

[server-9001.properties,server-9002.properties,server-9003.properties](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_13/kafka-cluster/config)

### 测试是否集群是否成功

创建topic

```shell
[root@192 kafka_2.13-2.7.0]# bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test32 --partitions 3 --replication-factor 2
Created topic test32.
[root@192 kafka_2.13-2.7.0]# bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test32
Topic: test32   PartitionCount: 3       ReplicationFactor: 2    Configs:
        Topic: test32   Partition: 0    Leader: 3       Replicas: 3,1   Isr: 3,1
        Topic: test32   Partition: 1    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: test32   Partition: 2    Leader: 2       Replicas: 2,3   Isr: 2,3

```

发送消息

```shell
[root@192 kafka_2.13-2.7.0]# bin/kafka-console-producer.sh --bootstrap-server localhost:9001,localhost:9002,localhost:9003 --topic test32
>222223
>333222
>
>33222
>
```

消费消息

```shell
[root@192 kafka_2.13-2.7.0]# bin/kafka-console-consumer.sh --bootstrap-server localhost:9001,localhost:9002,localhost:9003  --from-beginning --topic test32
222223
333222

33222
^CProcessed a total of 4 messages
```

### 测试性能

发送消息压测

```shell
[root@192 kafka_2.13-2.7.0]# bin/kafka-producer-perf-test.sh --topic test32 --num-records 100000 --record-size 1000 --throughput 2000 --producer-props bootstrap.servers=localhost:9002
9994 records sent, 1998.8 records/sec (1.91 MB/sec), 38.2 ms avg latency, 421.0 ms max latency.
10008 records sent, 2001.2 records/sec (1.91 MB/sec), 3.1 ms avg latency, 45.0 ms max latency.
9998 records sent, 1997.2 records/sec (1.90 MB/sec), 3.2 ms avg latency, 42.0 ms max latency.
10022 records sent, 2004.0 records/sec (1.91 MB/sec), 3.2 ms avg latency, 32.0 ms max latency.
10000 records sent, 2000.0 records/sec (1.91 MB/sec), 1.7 ms avg latency, 20.0 ms max latency.
10004 records sent, 2000.4 records/sec (1.91 MB/sec), 4.4 ms avg latency, 112.0 ms max latency.
9946 records sent, 1989.2 records/sec (1.90 MB/sec), 2.9 ms avg latency, 41.0 ms max latency.
10052 records sent, 2010.4 records/sec (1.92 MB/sec), 1.0 ms avg latency, 25.0 ms max latency.
10006 records sent, 2000.4 records/sec (1.91 MB/sec), 0.8 ms avg latency, 12.0 ms max latency.
100000 records sent, 1998.441216 records/sec (1.91 MB/sec), 5.96 ms avg latency, 421.00 ms max latency, 1 ms 50th, 17 ms 95th, 163 ms 99th, 326 ms 99.9th.
```

消费消息压测

```shell
[root@192 kafka_2.13-2.7.0]# bin/kafka-consumer-perf-test.sh --bootstrap-server localhost:9002 --topic test32 --fetch-size 1048576 --messages 100000 --threads 1
WARNING: option [threads] and [num-fetch-threads] have been deprecated and will be ignored by the test
start.time, end.time, data.consumed.in.MB, MB.sec, data.consumed.in.nMsg, nMsg.sec, rebalance.time.ms, fetch.time.ms, fetch.MB.sec, fetch.nMsg.sec
2021-01-11 22:53:03:974, 2021-01-11 22:53:05:696, 95.3674, 55.3818, 100004, 58074.3322, 1610376784421, -1610376782699, -0.0000, -0.0001
```

### Spring kafka对kafka集群操作代码

### 生产端

配置：[application.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/kafka-demo/kafka-producer/src/main/resources/application.yaml)

代码：[KafkaMessageController](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/kafka-demo/kafka-consumer/src/main/java/pers/cocoade/learning/kafka/consumer/KafkaMessageController.java)

运行结果：

```java
2021-01-11 23:59:01.215  INFO 18856 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
	acks = 1
	batch.size = 16384
	bootstrap.servers = [192.168.3.32:9001, 192.168.3.32:9002, 192.168.3.32:9003]
	buffer.memory = 33554432
	client.dns.lookup = default
	client.id = producer-1
        ...
```

### 消费端

配置：[application.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/kafka-demo/kafka-consumer/src/main/resources/application.yaml)

代码：[ProducerService](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_13/homework/kafka-demo/kafka-producer/src/main/java/pers/cocoadel/learning/kafka/producer/ProducerService.java)

运行结果：

```java
2021-01-11 23:58:45.442  INFO 4160 --- [ntainer#0-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : pers.ocoadel: partitions assigned: [order.test-0]
2021-01-11 23:59:01.770  INFO 4160 --- [ntainer#0-0-C-1] p.c.l.k.consumer.KafkaMessageController  : receive order: Order(id=1, ts=1, symbol=symbol-1, price=10.0)
2021-01-11 23:59:01.771  INFO 4160 --- [ntainer#0-0-C-1] p.c.l.k.consumer.KafkaMessageController  : receive order: Order(id=2, ts=2, symbol=symbol-2, price=10.0)
    ...
```



### 







