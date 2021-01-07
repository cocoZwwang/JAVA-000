## 1.（必做）配置 redis 的主从复制，sentinel 高可用，Cluster 集群。

- config配置文件

  - 主从复制
    - master [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/master/redis.conf)
    - slave [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/slave/redis.conf)
    - docker-compose.yaml
  - sentinel 高可用
    - master
    - slave1
    - slave2
    - sentinel1
    - sentinel2
    - docker-compose.yaml
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

- （选做）练习示例代码里下列类中的作业题：
  08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java

