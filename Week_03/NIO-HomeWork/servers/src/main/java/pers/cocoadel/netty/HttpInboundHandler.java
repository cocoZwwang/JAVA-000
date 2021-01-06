package pers.cocoadel.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        String uri = fullHttpRequest.uri();
        System.out.println("接收到请求uri: " + uri);
        handlerReq(fullHttpRequest, ctx);
    }

    private void handlerReq(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
        try {
            String value = fullHttpRequest.headers().get("nio");
            String content = String.format("hello %s! I am Netty Server !",value);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)));
            response.headers().add(fullHttpRequest.headers());
//            response.headers().add("Content-Type","text/html;charset=utf-8");
            response.headers().add("Content-Length",response.content().readableBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("处理接口出错！");
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NO_CONTENT);
        }finally {
            if (HttpUtil.isKeepAlive(fullHttpRequest)) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().add(CONNECTION,KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
