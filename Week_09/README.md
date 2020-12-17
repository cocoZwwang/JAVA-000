### 3.（必做）改造自定义 RPC 的程序，提交到 GitHub：

- 尝试将服务端写死查找接口实现类变成泛型和反射

  [ReflectRpcfxResolver.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/api/ReflectRpcfxResolver.java)

- 尝试将客户端动态代理改成 AOP，添加异常处理

  使用CGIIB作为实现AOP的工具，代码：[RpcfxInterceptor.java](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/client/RpcfxInterceptor.java)

- 尝试使用 Netty+HTTP 作为 client 端传输方式

  调用：[RpcfxInterceptor](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/client/RpcfxInterceptor.java)#post(RpcfxRequest , String) -> [CommHttpClient](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/rpc/rpcfx-core/src/main/java/pers/cocoadel/learning/rpcfx/http/CommHttpClient.java) #postByJson(String ,String) -> [NettyHttpClient](https://github.com/cocoZwwang/JAVA-000/blob/main/Week_09/home-work/netty-http/src/main/java/pers/cocoadel/learning/http/NettyHttpClient.java) 

  NettyHttpClient Module路径：[home-work/netty-http](https://github.com/cocoZwwang/JAVA-000/tree/main/Week_09/home-work/netty-http)

