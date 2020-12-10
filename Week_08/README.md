## 1.（选做）分析前面作业设计的表，是否可以做垂直拆分。

- 可以做垂直拆分。可以把用户、商品、订单垂直拆分成三个模块。

## 2.（必做）设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github。

- 使用sharding-jdbc做水平拆分
- 代码：[homework\sub-database](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_08/homework/sub-database)

## 2.（必做）基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一），提交到 Github。

- 基于 hmily TCC实现的简单的分布式事务：[homework\hmily-demo-springcloud](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_08/homework/hmily-demo-springcloud)

