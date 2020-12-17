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
        ctx.close();
    }
}
