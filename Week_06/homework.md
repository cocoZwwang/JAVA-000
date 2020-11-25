## 2.（必做）基于电商交易场景（用户、商品、订单），设计一套简单的表结构，提交 DDL 的 SQL 文件到 Github（后面 2 周的作业依然要是用到这个表结构）。

- 用户表

  ```mysql
  CREATE TABLE `users` (
    `id` bigint(20) NOT NULL COMMENT '用户ID',
    `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
    `gender` int(11) NOT NULL COMMENT '用户性别',
    `member_type` int(255) NOT NULL COMMENT '会员类型',
    `balance` bigint(255) NOT NULL COMMENT '余额（分）',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by` bigint(20) DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

- 商品表

  ```mysql
  CREATE TABLE `products` (
    `id` bigint(20) NOT NULL COMMENT '商品ID',
    `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
    `class_id` bigint(20) DEFAULT NULL COMMENT '商品种类ID',
    `price` mediumtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品价格（分）',
    `stock` bigint(20) NOT NULL COMMENT '库存',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by` bigint(20) DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

- 商品种类表

  ```mysql
  CREATE TABLE `product_class` (
    `id` bigint(20) NOT NULL COMMENT '商品种类ID',
    `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品种类名称',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by` bigint(20) DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

- 订单表

  ```my
  CREATE TABLE `orders` (
    `id` bigint(20) NOT NULL COMMENT '订单ID',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `product_id` int(11) NOT NULL COMMENT '商品ID',
    `product_amount` int(11) NOT NULL COMMENT '商品数量',
    `state` int(11) NOT NULL COMMENT '订单状态',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '下单时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

- 订单快照表

  ```mysq
  CREATE TABLE `orders_snapshot` (
    `id` bigint(20) NOT NULL COMMENT '订单快照ID',
    `order_id` int(11) NOT NULL COMMENT '订单ID',
    `product_id` int(11) NOT NULL COMMENT '商品ID',
    `product_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品名称',
    `product_amount` int(11) DEFAULT NULL COMMENT '商品数量',
    `product_price` bigint(20) DEFAULT NULL COMMENT '商品下单时候的价格',
    `product_class_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品种类名称',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

- 订单日记表

  ```mysql
  CREATE TABLE `orders_logs` (
    `id` bigint(20) NOT NULL COMMENT '订单日志ID',
    `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
    `state` int(11) DEFAULT NULL COMMENT '订单状态',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
  ```

  