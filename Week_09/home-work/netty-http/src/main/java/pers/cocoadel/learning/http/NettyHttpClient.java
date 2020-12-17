package pers.cocoadel.learning.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import pers.cocoadel.learning.http.handler.HttpFutureHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NettyHttpClient {

    private final Bootstrap bootstrap;

    public NettyHttpClient() {
        try {
            EventLoopGroup workGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("httpClientCodec", new HttpClientCodec());
                            pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("HttpContentDecompressor", new HttpContentDecompressor());
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
        HttpUrlInfo httpUrlInfo = HttpUrlInfo.parse(request.getUrl());
        try {
            ChannelFuture channelFuture = getChannelFuture(httpUrlInfo.getHost(), httpUrlInfo.getPort());
            FullHttpRequest fullHttpRequest = mapToFullHttpRequest(request, httpUrlInfo, channelFuture.channel());
            return requestForFuture(channelFuture, fullHttpRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 同步请求 + 超时机制
     */
    public HttpResponse executor(HttpRequest request, long time, TimeUnit timeUnit) {
        HttpUrlInfo httpUrlInfo = HttpUrlInfo.parse(request.getUrl());
        try {
            ChannelFuture channelFuture = getChannelFuture(httpUrlInfo.getHost(), httpUrlInfo.getPort());
            FullHttpRequest fullHttpRequest = mapToFullHttpRequest(request, httpUrlInfo, channelFuture.channel());
            return requestForFuture(channelFuture, fullHttpRequest).get(time, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步请求
     * @return CompletableFuture<HttpResponse>
     */
    public CompletableFuture<HttpResponse> requestAsync(HttpRequest request) {
        HttpUrlInfo httpUrlInfo = HttpUrlInfo.parse(request.getUrl());
        return CompletableFuture.supplyAsync(() -> {
            ChannelFuture channelFuture = getChannelFuture(httpUrlInfo.getHost(), httpUrlInfo.getPort());
            FullHttpRequest fullHttpRequest = mapToFullHttpRequest(request, httpUrlInfo, channelFuture.channel());
            return requestForFuture(channelFuture, fullHttpRequest);
        }).thenApply(this::futureGet);
    }

    private ChannelFuture getChannelFuture(String host, int port) {
        try {
            return bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            throw  new RuntimeException(e);
        }
    }

    private HttpResponse futureGet(HttpResponseFuture future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Netty真正发送数据的方法
     */
    private HttpResponseFuture requestForFuture(ChannelFuture channelFuture, FullHttpRequest request) {
        HttpResponseFuture future = new HttpResponseFuture();
        //动态添加HttpFutureHandler到当前Channel pipeline中
        channelFuture.channel().pipeline().addLast("HttpFutureHandler", new HttpFutureHandler(future));
        //判断当前Channel是否可写
        if(channelFuture.channel().isActive() && channelFuture.channel().isWritable()){
            channelFuture.channel().writeAndFlush(request);
        }else{
            future.setFailure(new RuntimeException("http channel is unWriteable!"));
        }
        return future;
    }

    /**
     * 用户请求转换成FullHttpRequest
     */
    private FullHttpRequest mapToFullHttpRequest(HttpRequest request, HttpUrlInfo httpUrlInfo, Channel channel) {
        ByteBuf byteBuf = channel.alloc().buffer(request.getContent() == null ? 0 : request.getContent().length);
        if (request.getContent() != null && request.getContent().length > 0) {
            byteBuf.writeBytes(request.getContent());
        }

        FullHttpRequest fullHttpRequest =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, request.getHttpMethod(), httpUrlInfo.getPath(), byteBuf);
        String address = httpUrlInfo.getHost() + ":" + httpUrlInfo.getPort();
        if (request.getHttpHeaders() != null) {
            fullHttpRequest.headers().add(request.getHttpHeaders());
        }
        fullHttpRequest.headers()
                .set(HttpHeaderNames.HOST, address)
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString())
                .set(HttpHeaderNames.USER_AGENT, "Netty Http Client/1.0")
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        return fullHttpRequest;
    }
}
