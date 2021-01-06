## 1.（必做）配置 redis 的主从复制，sentinel 高可用，Cluster 集群。

- config配置文件

  - 主从复制
    - master [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/master/redis.conf)
    - slave [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/slave/redis.conf)
  - sentinel 高可用
  - Cluster 集群

- 启动和操作、验证集群下数据读写的命令步骤。

  - 主从复制

    - 启动
      - master不需要做额外的改变，默认配置
      - slave配置文件添加一行：slaveof redis-master 6379。我这里使用docker-compose，直接就是使用服务名称即可。
      - 启动docker-compose

    - 验证

      - 连接master

        ```cmd
        H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
        192.168.3.100:6379>
        ```

      - 连接slave

        ```cmd
        H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6380
        192.168.3.100:6380>
        ```

      - maser写入数据

        ```cmd
        192.168.3.100:6379> set a 100
        OK
        ```

      - slave读取数据

        ```cmd
        192.168.3.100:6380> get a
        "100"
        192.168.3.100:6380>
        ```

  - sentinel 高可用

  - Cluster 集群

- （选做）练习示例代码里下列类中的作业题：
  08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java

