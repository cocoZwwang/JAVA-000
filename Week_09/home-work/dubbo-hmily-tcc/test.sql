-- 美元账户表，通过state字段冻结
-- A、B两边各有一张表
CREATE TABLE `dollar_account` (
  `id` bigint(20) NOT NULL COMMENT '美元账户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '账户名称',
  `balance` bigint(20) DEFAULT NULL COMMENT '余额',
  `state` smallint(6) DEFAULT NULL COMMENT '状态（0：正常 1：冻结）',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 人民币账户表，通过state字段冻结
-- A、B两边各有一张表
CREATE TABLE `rmb_account` (
  `id` bigint(20) NOT NULL COMMENT '人民币账户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '账户名称',
  `balance` bigint(20) DEFAULT NULL COMMENT '余额',
  `state` smallint(6) DEFAULT NULL COMMENT '状态（0：正常 1：冻结）',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 交易记录
-- A、B两边各有一张表记录自己美元/人民币账户的收支，A、B两边的记录ID一致。
CREATE TABLE `transaction_bill` (
  `id` bigint(20) NOT NULL COMMENT '账单记录id',
  `buy_account` bigint(20) DEFAULT NULL COMMENT '买入的账户',
  `buy_amount` bigint(20) DEFAULT NULL COMMENT '买入金额',
  `buy_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '买入的币种',
  `seller_account` bigint(20) DEFAULT NULL COMMENT '转出的账户',
  `seller_amount` bigint(20) DEFAULT NULL COMMENT '转出的金额',
  `seller_currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '转出的币种',
  `state` smallint(6) DEFAULT NULL COMMENT '状态（0：完成 1：交易中 2：失败）',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;