##  2.（必做）思考和设计自定义 MQ 第二个版本或第三个版本，写代码实现其中至少一个功能点，把设计思路和实现代码，提交到 GitHub。

#### 第一个版本-内存Queue 

基于内存Queue实现生产和消费API（已经完成）

- 1）创建内存BlockingQueue，作为底层消息存储
- 2）定义Topic，支持多个Topic
- 3）定义Producer，支持Send消息
- 4）定义Consumer，支持Poll消息

这个版本老师的代码已经实现，我按照老师的思路抽象了一部分的接口，方便后面版本的扩展。

代码：[cmq-core](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_14/homework/cmq-core)

#### 第二个版本：自定义Queue

去掉内存Queue，设计自定义Queue，实现消息确认和消费offset

- 1）自定义内存Message数组模拟Queue。（已完成）
- 2）使用指针记录当前消息写入位置。（已完成）
- 3）对于每个命名消费者，用指针记录消费位置。（已完成）

代码：[cmq-v2](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_14/homework/cmq-v2)

#### 第三个版本：基于SpringMVC实现MQServer

拆分broker和client(包括producer和consumer)

- 1）将Queue保存到web server端  (完成)
- 2）设计消息读写API接口，确认接口，提交offset接口  (完成)
- 3）producer和consumer通过httpclient访问Queue  (完成)
- 4）实现消息确认，offset提交  (完成)
- 5）实现consumer从offset增量拉取  (未完成)

[cmq-exchange](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_14/homework/cmq-exchange)：协议通信和本地mq服务的交换层

代码：[cmq-v3](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_14/homework/cmq-v3)

测试：[cmq-test](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_14/homework/cmq-test)

#### 第四个版本：功能完善MQ

增加多种策略（各条之间没有关系，可以任意选择实现）

- 1）考虑实现消息过期，消息重试，消息定时投递等策略

- 2）考虑批量操作，包括读写，可以打包和压缩

- 3）考虑消息清理策略，包括定时清理，按容量清理、LRU等

- 4）考虑消息持久化，存入数据库，或WAL日志文件，或BookKeeper

- 5）考虑将spring mvc替换成netty下的tcp传输协议，rsocket/websocket

  - 实现 将spring mvc替换成netty下的tcp传输协议

  - 添加服务空闲连接检测：服务器一定时间秒数内（比如10s）接受不到channel的请求就断掉

  - 客户端加上write idle check + keeplive ： 客户端一定时间内（比如：5s）不发送数据就发送一个keeplive，保证客户端连接的不被断。

  - 添加账号密码认证

    