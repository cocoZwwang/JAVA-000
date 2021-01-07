## 1.（必做）配置 redis 的主从复制，sentinel 高可用，Cluster 集群。

### config配置文件

##### 主从复制

- master [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/master/redis.conf)
- slave [redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/slave/redis.conf)
- [docker-compose.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/master-slave/docker-compose.yaml)

##### sentinel 高可用

- master：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/master/redis.conf)
- slave1：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/slave1/redis.conf)
- slave2：[redis.cnf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/slave2/redis.conf)
- sentinel1：[sentinel.conf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/sentinel1/sentinel.conf)
- sentinel2：[sentinel.conf](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/sentinel2/sentinel.conf)
- [docker-compose.yaml](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_12/sentinel/docker-compose.yaml)

##### Cluster 集群

- [docker-compose.yaml](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_12/cluster)
- [redis1、redis2、redis3、redis4、redis5、redis6](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_12/cluster)

### 启动和操作、验证集群下数据读写的命令步骤

##### 主从复制

- 启动
  - master不需要做额外的改变，默认配置
  - slave配置文件添加一行：slaveof redis-master 6379。我这里使用docker-compose，直接就是使用服务名称即可。
  - 启动docker-compose

- 验证

  - 连接master

    ```shell
    H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
    192.168.3.100:6379>
    ```

  - 连接slave

    ```shell
    H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6380
    192.168.3.100:6380>
    ```

  - maser写入数据

    ```shell
    192.168.3.100:6379> set a 100
    OK
    ```

  - slave读取数据

    ```shell
    192.168.3.100:6380> get a
    "100"
    192.168.3.100:6380>
    ```

##### sentinel 高可用

- 启动docker-compose

- 先验证一主二从

  master:

  ```shell
  H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
  192.168.3.100:6379> set a 1
  OK
  192.168.3.100:6379>
  ```

  slave1:

  ```shell
  H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6380
  192.168.3.100:6380> get a
  "1"
  ```

  slave2:

  ```shell
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

  ```shell
  192.168.3.100:6380> info replication
  # Replication
  role:slave
  master_host:172.23.0.4
  master_port:6379
  ```

  ```shell
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

  ```powershell
  H:\Redis-x64-5.0.9>redis-cli -h 192.168.3.100 -p 6379
  192.168.3.100:6379> info replication
  # Replication
  role:slave
  ```

  master变成了slave

##### Cluster 集群

- 启动docker-compose，一共6个redis容器‘

- 通过redis-cli初始化Redis集群

  创建集群，下面这些IP是在docker-compose里面指定的容器的固定子网ip
  
  ```shell
    redis-cli --cluster create 172.19.0.79:6379 172.19.0.80:6380 172.19.0.81:6381 172.19.0.82:6382 172.19.0.83:6383 172.19.0.84:6384 --cluster-replicas 1
  ```

  ```shell
  [root@subway-centre cluster1]# docker exec -ti redis-6379 /bin/bash
  root@c22ae413bf6e:/data# redis-cli --cluster create 172.19.0.79:6379 172.19.0.80:6380 172.19.0.81:6381 172.19.0.82:6382 172.19.0.83:6383 172.19.0.84:6384 --cluster-replicas 1
  >>> Performing hash slots allocation on 6 nodes...
  Master[0] -> Slots 0 - 5460
  Master[1] -> Slots 5461 - 10922
  Master[2] -> Slots 10923 - 16383
  Adding replica 172.19.0.83:6383 to 172.19.0.79:6379
  Adding replica 172.19.0.84:6384 to 172.19.0.80:6380
  Adding replica 172.19.0.82:6382 to 172.19.0.81:6381
  M: fcad173d11b32b376c7484aab230c8cc021e2aa6 172.19.0.79:6379
     slots:[0-5460] (5461 slots) master
  M: 4c6b28c064423e80d964b15e1072cc6fb906bfd0 172.19.0.80:6380
     slots:[5461-10922] (5462 slots) master
  M: 493f828321a4d2fe79f18be5d779be08e9e9249d 172.19.0.81:6381
     slots:[10923-16383] (5461 slots) master
  S: 1271e6235759b967a6b92e8ccd484dcafd3bd57d 172.19.0.82:6382
     replicates 493f828321a4d2fe79f18be5d779be08e9e9249d
  S: 27d8db563391a303df60759aa14e93db6eb320ea 172.19.0.83:6383
     replicates fcad173d11b32b376c7484aab230c8cc021e2aa6
  S: 2ee5258a27fdbc0d1457aa907571c117f6100ed8 172.19.0.84:6384
     replicates 4c6b28c064423e80d964b15e1072cc6fb906bfd0
  Can I set the above configuration? (type 'yes' to accept): yes
  >>> Nodes configuration updated
  >>> Assign a different config epoch to each node
  >>> Sending CLUSTER MEET messages to join the cluster
  Waiting for the cluster to join
  ..
  ```
  
  这时候有可能会一直等待，并且可以看到最后有提示：Sending CLUSTER MEET messages to join the cluster
  
  登录6379 容器，分别cluster meet其他几个节点
  
  ```shell
  [root@subway-centre cluster1]# docker exec -ti redis-6379 /bin/bash
  root@28236ac8e13b:/data# cd /usr/local/bin/
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.79:6379
  OK
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.80:6380
  OK
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.81:6381
  OK
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.82:6382
  OK
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.83:6383
  OK
  root@28236ac8e13b:/usr/local/bin# redis-cli -c -p 6379 cluster meet 172.19.0.84:6384
  OK
  root@28236ac8e13b:/usr/local/bin#
  ```
  
  这时候再切回刚刚等待的界面，就可以发现创建完成了
  
  ```shell
  >>> Performing Cluster Check (using node 172.19.0.79:6379)
  M: fcad173d11b32b376c7484aab230c8cc021e2aa6 172.19.0.79:6379
     slots:[0-5460] (5461 slots) master
     1 additional replica(s)
  S: 2ee5258a27fdbc0d1457aa907571c117f6100ed8 172.19.0.84:6384
     slots: (0 slots) slave
     replicates 4c6b28c064423e80d964b15e1072cc6fb906bfd0
  S: 1271e6235759b967a6b92e8ccd484dcafd3bd57d 172.19.0.82:6382
     slots: (0 slots) slave
     replicates 493f828321a4d2fe79f18be5d779be08e9e9249d
  S: 27d8db563391a303df60759aa14e93db6eb320ea 172.19.0.83:6383
     slots: (0 slots) slave
     replicates fcad173d11b32b376c7484aab230c8cc021e2aa6
  M: 4c6b28c064423e80d964b15e1072cc6fb906bfd0 172.19.0.80:6380
     slots:[5461-10922] (5462 slots) master
     1 additional replica(s)
  M: 493f828321a4d2fe79f18be5d779be08e9e9249d 172.19.0.81:6381
     slots:[10923-16383] (5461 slots) master
     1 additional replica(s)
  [OK] All nodes agree about slots configuration.
  >>> Check for open slots...
  >>> Check slots coverage...
  [OK] All 16384 slots covered.
  ```
  
  验证
  
  ```shell
  [root@subway-centre cluster1]# docker exec -ti redis-6379 /bin/bash
  root@c22ae413bf6e:/data# redis-cli -c -p 6379
  127.0.0.1:6379> set a 1
  -> Redirected to slot [15495] located at 172.19.0.81:6381
  OK
  172.19.0.81:6381> set c 4
  -> Redirected to slot [7365] located at 172.19.0.80:6380
  OK
  172.19.0.80:6380> get a
  -> Redirected to slot [15495] located at 172.19.0.81:6381
  "1"
  172.19.0.81:6381>
  ```

### （选做）练习示例代码里下列类中的作业题：08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java