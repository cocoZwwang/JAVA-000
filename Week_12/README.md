## 1.（必做）配置 redis 的主从复制，sentinel 高可用，Cluster 集群。

- config配置文件

  - 主从复制
    - master [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/master/redis.conf)
    - slave [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/slave/redis.conf)
    - [docker-compose.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/docker-compose.yaml)
  - sentinel 高可用
    - master：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/master/redis.conf)
    - slave1：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/slave1/redis.conf)
    - slave2：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/slave2/redis.conf)
    - sentinel1：[sentinel.conf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/sentinel1/sentinel.conf)
    - sentinel2：[sentinel.conf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/sentinel2/sentinel.conf)
    - [docker-compose.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/docker-compose.yaml)
  - Cluster 集群
    - docker-compose.yaml
    - redis1、redis2、redis3、redis4、redis5、redis6

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

    - 启动docker-compose

    - 先验证一主二从

      master:

      ```cmd
      H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
      192.168.3.100:6379> set a 1
      OK
      192.168.3.100:6379>
      ```

      slave1:

      ```cmd
      H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6380
      192.168.3.100:6380> get a
      "1"
      ```

      slave2:

      ```cmd
      H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6381
      192.168.3.100:6381> get a
      "1"
      ```

    - 关闭master

      ```shell
      [root@subway-centre sentinel]# docker-compose ps
           Name                    Command               State                 Ports
      ---------------------------------------------------------------------------------------------
      redis-master      docker-entrypoint.sh redis ...   Up      0.0.0.0:6379->6379/tcp
      redis-sentinel1   docker-entrypoint.sh redis ...   Up      0.0.0.0:26379->26379/tcp, 6379/tcp
      redis-sentinel2   docker-entrypoint.sh redis ...   Up      0.0.0.0:26380->26379/tcp, 6379/tcp
      redis-slave1      docker-entrypoint.sh redis ...   Up      0.0.0.0:6380->6379/tcp
      redis-slave2      docker-entrypoint.sh redis ...   Up      0.0.0.0:6381->6379/tcp
      [root@subway-centre sentinel]# docker stop redis-master
      redis-master
      [root@subway-centre sentinel]# docker-compose ps
           Name                    Command               State                  Ports
      ----------------------------------------------------------------------------------------------
      redis-master      docker-entrypoint.sh redis ...   Exit 0
      redis-sentinel1   docker-entrypoint.sh redis ...   Up       0.0.0.0:26379->26379/tcp, 6379/tcp
      redis-sentinel2   docker-entrypoint.sh redis ...   Up       0.0.0.0:26380->26379/tcp, 6379/tcp
      redis-slave1      docker-entrypoint.sh redis ...   Up       0.0.0.0:6380->6379/tcp
      redis-slave2      docker-entrypoint.sh redis ...   Up       0.0.0.0:6381->6379/tcp
      ```

    - info replication查看那个slave被切换成master

      ```cmd
      192.168.3.100:6380> info replication
      # Replication
      role:slave
      master_host:172.23.0.4
      master_port:6379
      ```

      ```cmd
      H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6381
      192.168.3.100:6381> info replication
      # Replication
      role:master
      connected_slaves:1
      ```

      从上面可以看到slave2被切换成master了

    - 重新启动master

      ```shell
      [root@subway-centre sentinel]# docker-compose up -d
      Starting redis-master ...
      redis-slave2 is up-to-date
      redis-slave1 is up-to-date
      redis-sentinel2 is up-to-date
      Starting redis-master ... done
      ```

      ```cmd
      H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
      192.168.3.100:6379> info replication
      # Replication
      role:slave
      ```

      master变成了slave

  - Cluster 集群

    - 启动docker-compose，一共6个redis容器‘

    - 通过redis-cli初始化Redis集群

      ```shell
      redis-cli --cluster create 192.168.3.100:6379 192.168.3.100:6380 192.168.3.100:6479 192.168.3.100:6480 192.168.3.100:6579 192.168.3.100:6580 --cluster-replicas 1
      ```

      ```cmd
      [WARNING] Some slaves are in the same host as their master
      M: 62be0c60a7f56fcb55aad17745df69754933c0a4 192.168.3.100:6379
         slots:[0-5460] (5461 slots) master
      M: 8b172f75540e749ed7ee62770fc8278515543567 192.168.3.100:6380
         slots:[5461-10922] (5462 slots) master
      M: 334f4942367d7870b9611cbbeaa82cfffb9c1691 192.168.3.100:6479
         slots:[10923-16383] (5461 slots) master
      S: 39ef3507c121eaa345a4cd2add7b3f9c384ad21e 192.168.3.100:6480
         replicates 334f4942367d7870b9611cbbeaa82cfffb9c1691
      S: d09cfe2eaec988c50309c36e658602c3a031f012 192.168.3.100:6579
         replicates 62be0c60a7f56fcb55aad17745df69754933c0a4
      S: 0a03b118d2385730531206d3014fd1ff6e0eba35 192.168.3.100:6580
         replicates 8b172f75540e749ed7ee62770fc8278515543567
      Can I set the above configuration? (type 'yes' to accept): yes
      >>> Nodes configuration updated
      >>> Assign a different config epoch to each node
      >>> Sending CLUSTER MEET messages to join the cluster
      Waiting for the cluster to join
      ```

- （选做）练习示例代码里下列类中的作业题：
  08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java

