##  2.（必做）思考和设计自定义 MQ 第二个版本或第三个版本，写代码实现其中至少一个功能点，把设计思路和实现代码，提交到 GitHub。

#### 第一个版本-内存Queue 

基于内存Queue实现生产和消费API（已经完成）

- 1）创建内存BlockingQueue，作为底层消息存储
- 2）定义Topic，支持多个Topic
- 3）定义Producer，支持Send消息
- 4）定义Consumer，支持Poll消息