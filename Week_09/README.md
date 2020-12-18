### 3.（必做）改造自定义 RPC 的程序，提交到 GitHub：

- 尝试将服务端写死查找接口实现类变成泛型和反射

  [ReflectRpcfxResolver.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/api/ReflectRpcfxResolver.java)

- 尝试将客户端动态代理改成 AOP，添加异常处理

  使用CGIIB作为实现AOP的工具：[RpcfxInterceptor.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/client/RpcfxInterceptor.java)

- 尝试使用 Netty+HTTP 作为 client 端传输方式

  调用：[RpcfxInterceptor#post(RpcfxRequest , String)](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/client/RpcfxInterceptor.java) -> [CommHttpClient #postByJson(String ,String)](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/http/CommHttpClient.java) -> [NettyHttpClient](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/netty-http/src/main/java/pers/cocoadel/learning/http/NettyHttpClient.java) 

  NettyHttpClient Module路径：[home-work/netty-http](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_09/home-work/netty-http)

  - 通过单个Channel复用，不需要每次请求都重新创建连接，但是一次只能一次会话，新的请求必须等待前一次会话结束。

  - 主要的NettyHandler处理链：

    - WriteIdleStateHandler：一定时间内（默认60秒）没有请求发送，关闭连接。

    - HttpRestHandler：负责发送请求和处理返回，和一次Http会话动态绑定。需要发送请求时候，动态add到Channel Pipeline中，通过addhandler回调发送消息，收到回复后处理返回结果并且动态删除。

    - HttpRequestToFullRequestHandler：用户请求转换成FullHttpRequest

    - DefaultHttpChannelPool：连接池，尝试通过监听netty内置事件来记录活动连接，实现连接池，但是还没写好，有bug，而且感觉API就没设计好，后面要继续修改。

  - 总之还有很多Bug和没写完的地方，只能后面慢慢完善。



### 3.（必做）结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:

- 用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
- 用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
- 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。

表设计：test.sql

dubbo+hmily实现TCC：TransactionServiceAImpl#testTCCTransaction(TransactionBill)

