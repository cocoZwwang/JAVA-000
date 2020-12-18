package pers.cocoadel.learning.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import pers.cocoadel.learning.http.handler.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NettyHttpClient {

    private final Bootstrap bootstrap;

    private final HttpChannelPool httpChannelPool;

    private final MetricHandler metricHandler;

    private final static long LIMIT = 100 * 1024 * 1024;

    private final GlobalChannelTrafficShapingHandler globalChannelTrafficShapingHandler =
            new GlobalChannelTrafficShapingHandler(new NioEventLoopGroup(), LIMIT, LIMIT, LIMIT, LIMIT);

    public NettyHttpClient() {
        try {
            EventLoopGroup workGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            httpChannelPool = new DefaultHttpChannelPool(bootstrap);
            metricHandler = new MetricHandler(httpChannelPool);
            bootstrap.channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("globalChannelTrafficShapingHandler",globalChannelTrafficShapingHandler);
                            pipeline.addLast("HttpClientCodec", new HttpClientCodec());
                            pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("HttpContentDecompressor", new HttpContentDecompressor());
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast("HttpRequestToFullRequestHandler", new HttpRequestToFullRequestHandler());
                            pipeline.addLast("HttpChannelPool", (ChannelHandler) httpChannelPool);
                            pipeline.addLast("WriteIdleStateHandler", new WriteIdleStateHandler(60));

                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 同步请求
     */
    public HttpResponse executor(HttpRequest request) {
        try {
            return executorForFuture(request).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 同步请求 + 超时机制
     */
    public HttpResponse executor(HttpRequest request, long time, TimeUnit timeUnit) {
        try {
            return executorForFuture(request).get(time, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步请求
     *
     * @return CompletableFuture<HttpResponse>
     */
    public CompletableFuture<HttpResponse> executorAsync(HttpRequest request) {
        return CompletableFuture
                .supplyAsync(() -> executorForFuture(request))
                .thenApply(this::futureGet);
    }

    /**
     * 真正发起请求的方法
     * 返回一个future
     */
    private HttpResponseFuture executorForFuture(HttpRequest request) {
        HttpResponseFuture future = new HttpResponseFuture();
        //动态添加HttpFutureHandler到当前Channel pipeline中
        HttpUrlInfo httpUrlInfo = request.parseUrl();
        Channel channel = getChannel(httpUrlInfo.getHost(), httpUrlInfo.getPort());
        channel.pipeline()
                .addBefore("HttpChannelPool","HttpFutureHandler", new HttpRestHandler(future, request));
//        channel.pipeline().addLast("HttpFutureHandler", new HttpRestHandler(future, request));
        return future;
    }

    private HttpResponse futureGet(HttpResponseFuture future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Channel getChannel(String host, int port) {
        return httpChannelPool.getChannel(host, port);
    }
}
