package pers.cocoadel.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

public class GatewayOutBoundHandler extends ChannelInboundHandlerAdapter {

    private final FullHttpRequest fullHttpRequest;

    private FullHttpResponse response;

    public GatewayOutBoundHandler( FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(fullHttpRequest);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        ByteBuf content = response.content();
        String s = content.toString(CharsetUtil.UTF_8);
        System.out.println("代理服务器返回：" + s);
        //网关返回给客户端
//        gateWayHandlerCtx.writeAndFlush(response);
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(s.getBytes(StandardCharsets.UTF_8)));
        response.headers().add("Content-Type","text/html;charset=utf-8");
        response.headers().add("Content-Length",response.content().readableBytes());
        this.response = response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public FullHttpResponse getResponse() {
        return response;
    }
}
