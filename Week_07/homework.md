# Week07 作业题目（周四）：

## 2、（必做）按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率

## 3、（选做）按自己设计的表结构，插入 1000 万订单模拟数据，测试不同方式的插入效

**2、3题放在一起测试。**

先通过存储过程生成用户、商品信息，测试插入效率主要通过后面订单表来测试

- 随机生成用户过程

  随机生成用户姓名：

  ```mysql
  CREATE DEFINER=`root`@`%` FUNCTION `randname`() RETURNS varchar(255) CHARSET utf8mb4 COLLATE utf8mb4_general_ci
  BEGIN
  	DECLARE char_str1 VARCHAR(100) DEFAULT 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  	DECLARE char_str2 VARCHAR(100) DEFAULT 'abcdefghijklmnopqrstuvwxyz';
  	DECLARE return_str VARCHAR(255) DEFAULT '';
  	DECLARE i INT DEFAULT 0;
  	DECLARE n int DEFAULT 5;
  -- 	firstName
  	set return_str := SUBSTRING(char_str1,FLOOR(1+RAND() * 26),1);
  	set n := FLOOR(1 + RAND() * 5);
  	WHILE i < n DO
  		SET return_str := CONCAT(return_str,SUBSTRING(char_str2,FLOOR(1+RAND() * 26),1));
  		SET i = i + 1;
  	END WHILE;
  -- 	lastName
  	set return_str := CONCAT(return_str,' ');
  	set return_str := CONCAT(return_str,SUBSTRING(char_str1,FLOOR(1+RAND() * 26),1));
  	set n := FLOOR(1+RAND() * 5);
  	set i := 0;
  	WHILE i < n DO
  		SET return_str := CONCAT(return_str,SUBSTRING(char_str2,FLOOR(1+RAND() * 26),1));
  		SET i = i + 1;
  	END WHILE;
  	RETURN return_str;
  END
  ```

  生成用户数据：

  ```mysql
  CREATE DEFINER=`root`@`%` PROCEDURE `createUsers`(num int)
  BEGIN
  	DECLARE i INT DEFAULT 0;
  	DECLARE d TIMESTAMP DEFAULT now();
  	DECLARE price BIGINT DEFAULT 0;
  	declare stock int default 0;
  	DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; SELECT -1; END;
  	START TRANSACTION;
  	REPEAT
  		SET i = i + 1;
  		set d := now();
  		INSERT INTO users VALUES(i,randname(),i % 2,i % 2,d,d,1,1);
  	UNTIL i = num END REPEAT;
  	COMMIT;
  END
  ```

  生成1w条用户数据：

  ```mysql
  call createUsers(10000);
  ```

  查询结果：

  ```mysql
  mysql> select count(*) from users;
  +----------+
  | count(*) |
  +----------+
  |    10000 |
  +----------+
  1 row in set (0.01 sec)
  
  mysql> select * from users where id < 10;
  +----+--------------+--------+-------------+---------------------+---------------------+-----------+-----------+
  | id | name         | gender | member_type | create_time         | update_time         | create_by | update_by |
  +----+--------------+--------+-------------+---------------------+---------------------+-----------+-----------+
  |  1 | Kkzov Omr    |      1 |           1 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  2 | Ztyjck Rq    |      0 |           0 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  3 | Tmplnf Lbhgj |      1 |           1 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  4 | Ddel Tos     |      0 |           0 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  5 | Vt Ri        |      1 |           1 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  6 | Nyfe Gdjky   |      0 |           0 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  7 | Mejj Puelup  |      1 |           1 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  8 | Ov Vuykc     |      0 |           0 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  |  9 | Hy Ey        |      1 |           1 | 2020-12-01 20:40:29 | 2020-12-01 20:40:29 |         1 |         1 |
  +----+--------------+--------+-------------+---------------------+---------------------+-----------+-----------+
  9 rows in set (0.00 sec)
  ```

  


- 随机生成商品种类存储过程

  ```mysq
  CREATE DEFINER=`root`@`%` PROCEDURE `createProductClass`(num int)
  BEGIN
  	DECLARE i INT DEFAULT 0;
  	DECLARE d TIMESTAMP DEFAULT now();
  	DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; SELECT -1; END;
  	START TRANSACTION;
  	REPEAT
  		SET i = i + 1;
  		SET d = now();
  		INSERT INTO product_class VALUES(i,CONCAT('商品类别-',i),d,d,1,1);
  	UNTIL i = num END REPEAT;
  	COMMIT;	
  END
  ```

  创建100条商品种类数据：

  ```mysql
  call createProductClass(100);
  ```

  查询结果：

  ```mysql
  mysql> select  count(*) from product_class;
  +----------+
  | count(*) |
  +----------+
  |      100 |
  +----------+
  1 row in set (0.02 sec)
  
  mysql> select * from product_class where id < 10;
  +----+------------+---------------------+---------------------+-----------+-----------+
  | id | name       | create_time         | update_time         | create_by | update_by |
  +----+------------+---------------------+---------------------+-----------+-----------+
  |  1 | 商品类别-1 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  2 | 商品类别-2 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  3 | 商品类别-3 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  4 | 商品类别-4 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  5 | 商品类别-5 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  6 | 商品类别-6 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  7 | 商品类别-7 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  8 | 商品类别-8 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  |  9 | 商品类别-9 | 2020-12-01 15:02:21 | 2020-12-01 15:02:21 |         1 |         1 |
  +----+------------+---------------------+---------------------+-----------+-----------+
  9 rows in set (0.00 sec)
  ```

  

- 随机生成商品过程

  随机生成价格函数（让多数商品价格集中在10 - 1000元）：

  ```mysql
  CREATE DEFINER=`root`@`%` FUNCTION `randPrice`() RETURNS bigint(64)
  BEGIN
  	DECLARE price int DEFAULT 0;
  	DECLARE max int DEFAULT 1000000;
  	DECLARE addend int DEFAULT 1000;
  	DECLARE rate DOUBLE DEFAULT 0.9;
  	DECLARE r DOUBLE DEFAULT 0.1;
  -- 	10元以下（单位/分）
  	set r:= RAND();
  	if r < 0.1 then 
  		set r := r + 0.1;
  	end if;
  	set price := price + FLOOR(r * addend);
  -- 
  	while price < 100000 and RAND() < rate do
  		set addend := addend * 10;	
  		set r:= RAND();
  		if r < 0.1 then 
  			set r := r + 0.1;
  		end if;
  		set price := price + FLOOR(r * addend);
  		set rate := rate * 2 - 1.1;
  	end while;
  	RETURN price;
  END
  ```

  随机生成商品：

  ```mys
  CREATE DEFINER=`root`@`%` PROCEDURE `createProduct`(num int)
  BEGIN
  	DECLARE i INT DEFAULT 0;
  	DECLARE d TIMESTAMP DEFAULT now();
  	DECLARE price BIGINT DEFAULT 0;
  	declare stock int default 0;
  	DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; SELECT -1; END;
  	START TRANSACTION;
  	REPEAT
  		SET i = i + 1;
  		SET d = now();
  		set price := randPrice();
  -- 		库存和价格成反比
  		set stock := FLOOR(10000000 / price);
  		INSERT INTO products VALUES(i,CONCAT('商品-',i),i % 100 + 1,price,stock,d,d,1,1);
  	UNTIL i = num END REPEAT;
  	COMMIT;
  END
  ```

  生成2000条商品数据：

  ```mysql
  call createProduct(2000);
  ```

  查询结果：

  ```mysql
  mysql> select count(*) from  products
      -> ;
  +----------+
  | count(*) |
  +----------+
  |     2000 |
  +----------+
  1 row in set (0.05 sec)
  
  mysql> select * from products where id < 10;
  +----+--------+----------+---------+-------+---------------------+---------------------+-----------+-----------+
  | id | name   | class_id | price   | stock | create_time         | update_time         | create_by | update_by |
  +----+--------+----------+---------+-------+---------------------+---------------------+-----------+-----------+
  |  1 | 商品-1 |        2 | 1060540 |     9 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  2 | 商品-2 |        3 |   65559 |   152 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  3 | 商品-3 |        4 |   20273 |   493 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  4 | 商品-4 |        5 |  662225 |    15 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  5 | 商品-5 |        6 |  366897 |    27 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  6 | 商品-6 |        7 |   16760 |   596 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  7 | 商品-7 |        8 |     544 | 18382 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  8 | 商品-8 |        9 |     169 | 59171 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  |  9 | 商品-9 |       10 |    4107 |  2434 | 2020-12-01 15:37:38 | 2020-12-01 15:37:38 |         1 |         1 |
  +----+--------+----------+---------+-------+---------------------+---------------------+-----------+-----------+
  9 rows in set (0.01 sec)
  ```
#### 通过Mysql存储过程插入订单

随机日期函数，让订单日期在一年内尽量均匀：

```mysql
CREATE DEFINER=`root`@`%` FUNCTION `randTime`(startDate VARCHAR(10),endDate VARCHAR(10)) RETURNS timestamp
BEGIN
	DECLARE v_min TIMESTAMP DEFAULT now();
	DECLARE v_max TIMESTAMP DEFAULT now();
	DECLARE v_max_min TIMESTAMP DEFAULT 1;
	SET v_min := UNIX_TIMESTAMP(DATE_FORMAT(v_start_date,'%d/%m/%Y'));
	SET v_max := UNIX_TIMESTAMP(DATE_FORMAT(v_end_date,'%d/%m/%Y'));
	SET v_max_min := v_max - v_min;
	return FLOOR(RAND()* v_max_min + v_min);
END
```

随机插入订单存储过程：

```mysql
CREATE DEFINER=`root`@`%` PROCEDURE `insertOrder`(start_i int,num int)
BEGIN
	DECLARE i INT DEFAULT 0;
	DECLARE d TIMESTAMP DEFAULT now();
	DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; SELECT -1; END;
	START TRANSACTION;
		REPEAT
			SET i = i + 1;
			SET d = ADDDATE('2020-01-01', FLOOR(RAND() * 365));
			INSERT INTO orders VALUES(start_i + i,FLOOR(1 + RAND() * 10000),FLOOR(1 + RAND() * 2000),FLOOR(1 + RAND() * 10),0,d,d);
		UNTIL i = num END REPEAT;
	COMMIT;
END
```

##### 随机生成100W订单数据：

```mysql
mysql> call insertOrder(0,1000000);
Query OK, 0 rows affected (48.84 sec)

mysql> select count(*) from orders;
+----------+
| count(*) |
+----------+
|  1000000 |
+----------+
1 row in set (0.04 sec)

mysql> select * from orders where id < 10;
+----+---------+------------+----------------+-------+---------------------+---------------------+
| id | user_id | product_id | product_amount | state | create_time         | update_time         |
+----+---------+------------+----------------+-------+---------------------+---------------------+
|  1 |    5191 |        184 |             10 |     0 | 2020-03-02 00:00:00 | 2020-03-02 00:00:00 |
|  2 |    4420 |       1047 |              3 |     0 | 2020-03-24 00:00:00 | 2020-03-24 00:00:00 |
|  3 |    5375 |         85 |              6 |     0 | 2020-11-17 00:00:00 | 2020-11-17 00:00:00 |
|  4 |    5362 |        164 |              8 |     0 | 2020-11-12 00:00:00 | 2020-11-12 00:00:00 |
|  5 |    3728 |       1201 |              9 |     0 | 2020-10-02 00:00:00 | 2020-10-02 00:00:00 |
|  6 |    4276 |        583 |              2 |     0 | 2020-08-12 00:00:00 | 2020-08-12 00:00:00 |
|  7 |    4601 |        622 |              2 |     0 | 2020-12-29 00:00:00 | 2020-12-29 00:00:00 |
|  8 |    1699 |         68 |              7 |     0 | 2020-12-08 00:00:00 | 2020-12-08 00:00:00 |
|  9 |    9826 |        677 |              8 |     0 | 2020-03-10 00:00:00 | 2020-03-10 00:00:00 |
+----+---------+------------+----------------+-------+---------------------+---------------------+
9 rows in set (0.00 sec)
```

##### 随机生成1000W数据：

```mysql
mysql> call insertOrder(0,10000000);
Query OK, 0 rows affected (9 min 17.82 sec)

mysql> select count(*) from orders;
+----------+
| count(*) |
+----------+
| 10000000 |
+----------+
1 row in set (5.69 sec)

mysql> select * from orders where id > 9999990;
+----------+---------+------------+----------------+-------+---------------------+---------------------+
| id       | user_id | product_id | product_amount | state | create_time         | update_time         |
+----------+---------+------------+----------------+-------+---------------------+---------------------+
|  9999991 |    1282 |       1772 |              1 |     0 | 2020-12-01 00:00:00 | 2020-12-01 00:00:00 |
|  9999992 |    6808 |       1433 |              6 |     0 | 2020-07-24 00:00:00 | 2020-07-24 00:00:00 |
|  9999993 |    1171 |       1890 |              4 |     0 | 2020-07-18 00:00:00 | 2020-07-18 00:00:00 |
|  9999994 |     220 |         53 |              1 |     0 | 2020-01-11 00:00:00 | 2020-01-11 00:00:00 |
|  9999995 |     540 |       1038 |              5 |     0 | 2020-04-01 00:00:00 | 2020-04-01 00:00:00 |
|  9999996 |    7092 |       1488 |              6 |     0 | 2020-08-07 00:00:00 | 2020-08-07 00:00:00 |
|  9999997 |    8680 |        307 |              2 |     0 | 2020-09-23 00:00:00 | 2020-09-23 00:00:00 |
|  9999998 |    2716 |        606 |              7 |     0 | 2020-05-08 00:00:00 | 2020-05-08 00:00:00 |
|  9999999 |    8422 |        894 |              8 |     0 | 2020-08-02 00:00:00 | 2020-08-02 00:00:00 |
| 10000000 |    8649 |       1464 |              1 |     0 | 2020-03-13 00:00:00 | 2020-03-13 00:00:00 |
+----------+---------+------------+----------------+-------+---------------------+---------------------+
10 rows in set (0.00 sec)
```



#### 通过JDBC批量插入订单数据

代码：[InsertDataBootstrap .class](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/order-demo/src/main/java/pers/cocoadel/learning/mysql/InsertDataBootstrap.java)

jdbc批量插入代码：

 ```java
@Override
public void batchSave(List<Order> orders) {
    if(CollectionUtils.isEmpty(orders)){
        return;
    }
    String sql = "insert into orders (id,user_id,product_id,product_amount,state,create_time,update_time) " +
        "values(?,?,?,?,?,?,?)";
    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Order order = orders.get(i);
            ps.setLong(1, order.getId());
            ps.setLong(2,order.getUserId());
            ps.setLong(3,order.getProductId());
            ps.setInt(4,order.getProductAmount());
            ps.setInt(5, order.getState());
            ps.setDate(6,new Date(order.getCreateTime().getTime()));
            ps.setDate(7,new Date(order.getUpdateTime().getTime()));
        }

        @Override
        public int getBatchSize() {
            return orders.size();
        }
    });
}
 ```

##### 随机插入100W数据：

```java
long time = System.currentTimeMillis();
List<Order> orders = createOrders(1,1000000);
orderDao.batchSave(orders);
System.out.printf("插入完成,耗时：%s s\n",(System.currentTimeMillis() - time) / 1000);
```

输出：

```java
2020-12-01 20:15:22.628  INFO 24368 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
插入完成,耗时：121 s
2020-12-01 20:17:22.213  INFO 24368 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
```

##### 随机插入1000W数据：

```java
2020-12-01 21:53:18.424  INFO 25488 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
插入完成,耗时：1284 s
2020-12-01 22:14:40.769  INFO 25488 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
```



#### 对比两种插入效率

| 插入方式  | 数据量 | 耗时(s) |
| --------- | ------ | ------- |
| 存储过程  | 100W   | 48.84   |
| Java JDBC | 100W   | 121     |
| 存储过程  | 1000W  | 557.82  |
| Java JDBC | 1000W  | 1284    |



## 6.（选做）尝试自己做一个 ID 生成器（可以模拟 Seq 或 Snowflake）

#### 定义IdCreator接口

```java
public interface IdCreator<T> {
    T nextId();
}
```

### 模拟sequence

- 创建sequence数据库

  ```java
  -- name sequence 名称
  -- current_value 当前 value
  -- increment 增长步长! 可理解为在数据库中一次读取多少个sequence. 当这些用完后, 下次再从数据库中读取。
  CREATE TABLE MY_SEQUENCE(`NAME` VARCHAR(50) NOT NULL,current_value INT NOT NULL,increment INT NOT NULL DEFAULT 100, PRIMARY KEY(NAME)) ENGINE=INNODB;
  ```

- 创建操作函数

  ```mysql
  -- 获取当前 sequence 的值 (返回当前值,增量)
  DROP FUNCTION IF EXISTS my_seq_currval;
  DELIMITER $$
  CREATE FUNCTION my_seq_currval(seq_name VARCHAR(50)) RETURNS varchar(64)
  DETERMINISTIC
  BEGIN
  	DECLARE retval VARCHAR(64);
  	SET retval="-999999999,null";
  	SELECT concat(CAST(current_value AS CHAR),",",CAST(increment AS CHAR)) INTO retval FROM
  	MY_SEQUENCE WHERE name = seq_name;
  RETURN retval;
  END $$
  DELIMITER;
  ```

  ```mysql
  -- 设置sequence值
  DROP FUNCTION IF EXISTS my_seq_setval;
  DELIMITER $$
  CREATE FUNCTION my_seq_setval(seq_name VARCHAR(50),value INTEGER) RETURNS varchar(64)
  DETERMINISTIC
  BEGIN
  	UPDATE MY_SEQUENCE SET current_value = value WHERE name = seq_name;
  RETURN my_seq_currval(seq_name);
  END $$
  DELIMITER;
  ```

  ```mysql
  -- 获取下一个sequence值
  DROP FUNCTION IF EXISTS my_seq_nextval;
  DELIMITER $$
  CREATE FUNCTION my_seq_nextval(seq_name VARCHAR(50)) RETURNS varchar(64)
  DETERMINISTIC
  BEGIN
  	UPDATE MY_SEQUENCE SET current_value = current_value + increment WHERE name = seq_name;
  	RETURN my_seq_currval(seq_name);
  END $$
  DELIMITER;
  ```

- 代码实现：[SequenceIdCreator.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/IdCreator/src/main/java/pers/cocoade/learning/mysql/SequenceIdCreator.java)

- 代码测试：[SequenceIdCreatorTest.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/IdCreator/src/test/java/pers/cocoade/learning/mysql/SequenceIdCreatorTest.java)

  

  ```java
  2020-12-02 17:51:19.273  INFO 20072 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
  2020-12-02 17:51:19.441  INFO 20072 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
  第0个id：101
  第1个id：102
  第2个id：103
  第3个id：104
  第4个id：105
  ...
  ```

### SnowFlake

- 代码实现：[MagicSnowFlake](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/IdCreator/src/main/java/pers/cocoade/learning/mysql/MagicSnowFlake.java)、[MagicSnowFlakeIdCreator](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/IdCreator/src/main/java/pers/cocoade/learning/mysql/MagicSnowFlakeIdCreator.java)

- 代码测试：[MagicSnowFlakeIdCreatorTest](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/IdCreator/src/test/java/pers/cocoade/learning/mysql/MagicSnowFlakeIdCreatorTest.java)

  ```java
  第0个SnowFlake id：518849359081635840
  第1个SnowFlake id：518849359081635841
  第2个SnowFlake id：518849359081635842
  第3个SnowFlake id：518849359081635843
  第4个SnowFlake id：518849359081635844
  第5个SnowFlake id：518849359081635845
  ...
  ```

  

# Week07 作业题目（周六）：

## 1.（选做）配置一遍异步复制，半同步复制、组复制

#### 异步复制

##### 主机信息

| server-id | 角色   | ip:port            |
| --------- | ------ | ------------------ |
| 1         | master | 192.168.3.100:3306 |
| 2         | slave  | 192.168.3.101:3301 |

##### master配置:

```
[client]
port=3306
[mysqld]
port=3306

server-id=1
max_connections=50000
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
# 
log-error=/var/log/mysql/mysqld.log
#
slow_query_log=true
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=5
#
pid-file=/var/run/mysqld/mysqld.pid

#设置不要复制的数据库 可设置多个
binlog-ignore-db=mysql
binlog-ignore-db=information_schema

#设置需要复制的数据库
binlog-do-db=test_db

#设置 logbin 格式
binlog_format=STATEMENT
#在作为从数据库的时候， 有写入操作 也要更新二进制日志文件
log-slave-updates
#表示自增长字段每次递增的量，指自增字段的起始值，其默认值是 1 取值范围是 1 .. 65535
auto_increment_increment=2
#表示自增长字段从哪个数开始，指字段一次递增多少，他的取值范围是 1 .. 65535
auto_increment_offset=1
```

##### slave配置:

```
[client]
port=3301
[mysqld]
port=3301

server-id=3
max_connections=50000
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
# 
log-error=/var/log/mysql/mysqld.log
#
slow_query_log=true
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=5
#
pid-file=/var/run/mysqld/mysqld.pid

relay-log=mysql-relay
```

##### 查询master的binlog信息：

```mysql
mysql> show master status;
+---------------+----------+--------------+--------------------------+-------------------+
| File          | Position | Binlog_Do_DB | Binlog_Ignore_DB         | Executed_Gtid_Set |
+---------------+----------+--------------+--------------------------+-------------------+
| binlog.000008 |    10589 | test_db      | mysql,information_schema |                   |
+---------------+----------+--------------+--------------------------+-------------------+
1 row in set (0.00 sec)
```

##### master上设置同步复制账号：

```mysql
GRANT REPLI CA TION SLAVE ON *.* TO 'slave'@'%' IDENTIFIED BY '123456';
```

##### slave上设置master的相关信息：

```mysql
mysql> change master to master_host='192.168.3.100',
    -> master_user='slave',
    -> master_password='123456',
    -> master_log_file='binlog.000008',
    -> master_log_pos=10589;
Query OK, 0 rows affected, 1 warning (0.38 sec)

mysql> start slave;
Query OK, 0 rows affected (0.11 sec)

mysql> show slave status\G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.3.100
                  Master_User: slave
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: binlog.000008
          Read_Master_Log_Pos: 10589
               Relay_Log_File: mysql-relay.000002
                Relay_Log_Pos: 319
        Relay_Master_Log_File: binlog.000008
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB:
```
Slave_IO_Running: Yes 和 Slave_SQL_Running: Yes表示主从复制配置成功。

##### 检测主从复制：在master上面通过语句创建一张表，在slave查看表是否存在。

master：

```mysql
mysql> CREATE TABLE `mytbl`  (  `id` int(11) NOT NULL,  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,  PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
Query OK, 0 rows affected (0.39 sec)

mysql> show tables;
+-------------------+
| Tables_in_test_db |
+-------------------+
| mytbl             |
| orders            |
| orders_logs       |
| orders_snapshot   |
| product_class     |
| products          |
| users             |
+-------------------+
7 rows in set (0.00 sec)
```

slave：

```mysql
mysql> show tables;
+-------------------+
| Tables_in_test_db |
+-------------------+
| mytbl             |
| orders            |
| orders_logs       |
| orders_snapshot   |
| product_class     |
| products          |
| users             |
+-------------------+
7 rows in set (0.00 sec)
```

## 2.（必做）读写分离 - 动态切换数据源版本 1.0

项目路径：[homework-code/multi-data-source](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_07/homework-code/multi-data-source)

##### 代码验证

service代码：

```java
   //默认走master
	@DataSource
    @Transactional
    public void batchSave(){
        List<Mytbl> list = createData(1,5);
        mytblDao.batchSave(list);
    }
	
	...

    /**
     * 标记读取数据的数据源
     */
    @DataSource(value = "slave1")
    @Transactional
    public void show(){
        List<Mytbl> list = mytblDao.findAll();
        for (Mytbl mytbl : list) {
            System.out.println(mytbl);
        }
    }
```

Unit Test 代码：

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class MytblServiceTest {
    @Autowired
    private MytblService mytblService;

    @Test
    public void batchSave(){
        mytblService.batchSave();
    }

    @Test
    public void show() {
        mytblService.show();
    }
}
```

先运行batchSave()方法，插入数据，再运行show()方法：

 ```
2020-12-02 05:42:42.598  INFO 25784 --- [           main] p.c.l.m.context.CustomMultiDataSource    : 当前数据源是：slave1
2020-12-02 05:42:42.598  INFO 25784 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Starting...
2020-12-02 05:42:42.606  INFO 25784 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Start completed.
Mytbl(id=1, name=master-0)
Mytbl(id=2, name=master-1)
Mytbl(id=3, name=master-2)
Mytbl(id=4, name=master-3)
Mytbl(id=5, name=master-4)
 ```

从上面看出数据插入成功。

手动修改slave上的数据：

```mysql
mysql> select * from mytbl;
+----+----------+
| id | name     |
+----+----------+
|  1 | master-0 |
|  2 | master-1 |
|  3 | master-2 |
|  4 | master-3 |
|  5 | master-4 |
+----+----------+
5 rows in set (0.00 sec)

mysql> update mytbl set name = 'slave-custom';
Query OK, 5 rows affected (0.02 sec)
Rows matched: 5  Changed: 5  Warnings: 0

mysql> select * from mytbl;
+----+--------------+
| id | name         |
+----+--------------+
|  1 | slave-custom |
|  2 | slave-custom |
|  3 | slave-custom |
|  4 | slave-custom |
|  5 | slave-custom |
+----+--------------+
5 rows in set (0.00 sec)
```

再单独运行show()方法：

```
2020-12-02 05:45:38.497  INFO 24812 --- [           main] p.c.l.m.context.CustomMultiDataSource    : 当前数据源是：slave1
2020-12-02 05:45:38.497  INFO 24812 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Starting...
2020-12-02 05:45:38.507  INFO 24812 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Start completed.
Mytbl(id=1, name=slave-custom)
Mytbl(id=2, name=slave-custom)
Mytbl(id=3, name=slave-custom)
Mytbl(id=4, name=slave-custom)
Mytbl(id=5, name=slave-custom)
```

从上面可以看出读取的确实是slave主机上的数据，因此一个自定义的动态切换数据源实现读写分离的功能就实现了。



## 3.（必做）读写分离 - 数据库框架版本 2.0

#### 使用sharding-jdbc

项目路径：[homework-code/sharding-jdbc-demo](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/sharding-jdbc-demo/src/test/java/pers/cocoadel/learning/mysql/service/MytblServiceTest.java)

测试代码和上面的几乎一样，先运行insert() 后运行show()：

```java
2020-12-02 07:25:51.492  INFO 26652 --- [           main] ShardingSphere-SQL                       : Actual SQL: slave0 ::: select mytbl0_.id as id1_0_, mytbl0_.name as name2_0_ from mytbl mytbl0_
Mytbl(id=1, name=master-0)
Mytbl(id=2, name=master-1)
Mytbl(id=3, name=master-2)
Mytbl(id=4, name=master-3)
Mytbl(id=5, name=master-4)
2020-12-02 07:25:51.505  INFO 26652 --- [extShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
```

和上面一样手动修改slave的数据：

```mysql
mysql> select * from mytbl;
+----+----------+
| id | name     |
+----+----------+
|  1 | master-0 |
|  2 | master-1 |
|  3 | master-2 |
|  4 | master-3 |
|  5 | master-4 |
+----+----------+
5 rows in set (0.00 sec)

mysql> update mytbl set name = 'slave-shardjdbc';
Query OK, 5 rows affected (0.00 sec)
Rows matched: 5  Changed: 5  Warnings: 0

mysql> select * from mytbl;
+----+-----------------+
| id | name            |
+----+-----------------+
|  1 | slave-shardjdbc |
|  2 | slave-shardjdbc |
|  3 | slave-shardjdbc |
|  4 | slave-shardjdbc |
|  5 | slave-shardjdbc |
+----+-----------------+
5 rows in set (0.00 sec)
```

再次运行show()方法：

```java
2020-12-02 07:27:58.686  INFO 20852 --- [           main] ShardingSphere-SQL                       : Actual SQL: slave0 ::: select mytbl0_.id as id1_0_, mytbl0_.name as name2_0_ from mytbl mytbl0_
Mytbl(id=1, name=slave-shardjdbc)
Mytbl(id=2, name=slave-shardjdbc)
Mytbl(id=3, name=slave-shardjdbc)
Mytbl(id=4, name=slave-shardjdbc)
Mytbl(id=5, name=slave-shardjdbc)
2020-12-02 07:27:58.733  INFO 20852 --- [extShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
```

sharding-jdbc验证读写分离成功

## 4.（选做）读写分离 - 数据库中间件版本 3.0

### 使用sharding-proxy

测试代码：[MytblServiceTest.class](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_07/homework-code/sharding-jdbc-demo/src/test/java/pers/cocoadel/learning/mysql/service/MytblServiceTest.java)

##### config-master_slave.yaml配置：

```
schemaName: master_slave_db

dataSources:
 master_ds:
   url: jdbc:mysql://192.168.3.100:3306/test_db?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
   username: root
   password: suntek
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
 slave_ds_0:
   url: jdbc:mysql://192.168.3.101:3301/test_db?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
   username: root
   password: suntek
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50

masterSlaveRule:
 name: ms_ds
 masterDataSourceName: master_ds
 slaveDataSourceNames:
   - slave_ds_0
```

##### 启动成功：

```cmd
H:\apache-shardingsphere-4.1.1-sharding-proxy-bin\bin>start.bat
Starting the Sharding-Proxy ...
01:47:59,190 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
.........
[INFO ] 01:47:59.370 [main] o.a.s.core.log.ConfigurationLogger - MasterSlaveRuleConfiguration:
masterDataSourceName: master_ds
name: ms_ds
slaveDataSourceNames:
- slave_ds_0

[INFO ] 01:47:59.426 [main] o.a.s.core.log.ConfigurationLogger - Authentication:
users:
  root:
    authorizedSchemas: ''
    password: root
  sharding:
    authorizedSchemas: sharding_db
    password: sharding

[INFO ] 01:47:59.427 [main] o.a.s.core.log.ConfigurationLogger - Properties:
{}

[INFO ] 01:47:59.548 [main] com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
[INFO ] 01:47:59.726 [main] com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
[INFO ] 01:47:59.726 [main] com.zaxxer.hikari.HikariDataSource - HikariPool-2 - Starting...
[INFO ] 01:47:59.736 [main] com.zaxxer.hikari.HikariDataSource - HikariPool-2 - Start completed.
```

##### 登录sharding-proxy：

```cmd
H:\Mysql\mysql-8.0.15-winx64\bin>mysql -uroot -P3307 -p
Enter password: ****
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 57
Server version: 8.0.20-Sharding-Proxy 4.1.0

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+-----------------+
| Database        |
+-----------------+
| master_slave_db |
+-----------------+
1 row in set (0.00 sec)

mysql> show tables;
+-------------------+
| Tables_in_test_db |
+-------------------+
| mytbl             |
| orders            |
| orders_logs       |
| orders_snapshot   |
| product_class     |
| products          |
| users             |
+-------------------+
7 rows in set (0.00 sec)
```

##### 通过代码验证读写分离：

先运行insert() 后运行show()：

```mysql
Mytbl(id=1, name=master-0)
Mytbl(id=2, name=master-1)
Mytbl(id=3, name=master-2)
Mytbl(id=4, name=master-3)
Mytbl(id=5, name=master-4)
2020-12-02 05:48:47.783  INFO 16936 --- [extShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2020-12-02 05:48:47.784  INFO 16936 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2020-12-02 05:48:47.796  INFO 16936 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```

为了验证读写分离，手动修改slave上的name字段：

```cmd
mysql> select * from mytbl;
+----+----------+
| id | name     |
+----+----------+
|  1 | master-0 |
|  2 | master-1 |
|  3 | master-2 |
|  4 | master-3 |
|  5 | master-4 |
+----+----------+
5 rows in set (0.00 sec)

mysql> update mytbl set name = 'slave-sharding';
Query OK, 5 rows affected (0.01 sec)
Rows matched: 5  Changed: 5  Warnings: 0

mysql> select * from mytbl;
+----+----------------+
| id | name           |
+----+----------------+
|  1 | slave-sharding |
|  2 | slave-sharding |
|  3 | slave-sharding |
|  4 | slave-sharding |
|  5 | slave-sharding |
+----+----------------+
5 rows in set (0.00 sec)
```

再单独运行show()方法：

```java
Mytbl(id=1, name=slave-sharding)
Mytbl(id=2, name=slave-sharding)
Mytbl(id=3, name=slave-sharding)
Mytbl(id=4, name=slave-sharding)
Mytbl(id=5, name=slave-sharding)
2020-12-02 05:52:46.076  INFO 25656 --- [extShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2020-12-02 05:52:46.078  INFO 25656 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2020-12-02 05:52:46.090  INFO 25656 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```

从上面可以看出读取的是slave的数据，表明sharding-proxy读写分离配置成功！