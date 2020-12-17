package pers.cocoadel.learning.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import pers.cocoadel.learning.http.HttpResponse;
import pers.cocoadel.learning.http.HttpResponseBody;
import pers.cocoadel.learning.http.HttpResponseFuture;

/**
 * Http结果返回处理
 * 每次会话和一个HttpFutureHandler绑定
 * http发送请求动态添加到Channel pipeline中
 * http返回则动态移除
 * 如果Channel通信发送移除，则当前Channel直接关闭
 */
public class HttpFutureHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final HttpResponseFuture future;

    public HttpFutureHandler(HttpResponseFuture future) {
        this.future = future;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.setAll(msg.headers());
        ByteBuf byteBuf = msg.content();
        msg.status();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,bytes);
        HttpResponse httpResponse = new HttpResponse.Builder()
                .httpHeaders(headers)
                .httpResponseBody(new HttpResponseBody(bytes))
                .status(msg.status().code())
                .message(msg.status().toString())
                .build();
        future.setSuccess(httpResponse);
        //一次Http会话结束，异常当前Handler对象
        ctx.pipeline().remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        future.setFailure(cause);
        ctx.close();
    }
}
