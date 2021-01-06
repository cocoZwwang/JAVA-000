package pers.cocoadel.learning.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import pers.cocoadel.learning.http.HttpRequest;
import pers.cocoadel.learning.http.HttpResponse;
import pers.cocoadel.learning.http.HttpResponseBody;
import pers.cocoadel.learning.http.HttpResponseFuture;
import pers.cocoadel.learning.http.event.HttpStageEvent;

import static pers.cocoadel.learning.http.event.HttpStageEvent.HTTP_STAGE_SENDING_EVENT;
import static pers.cocoadel.learning.http.event.HttpStageEvent.HTTP_STAGE_WAITING_EVENT;

/**
 * Http单channel复用
 * Http请求发起 -> Http结果返回处理，每次会话只会和一个HttpRestHandler绑定。
 * http发送请求动态添加到Channel pipeline中
 * http返回则动态移除
 * 如果Channel通信发生异常，则当前Channel直接关闭
 */
public class HttpRestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final HttpResponseFuture future;

    private final HttpRequest request;

    public HttpRestHandler(HttpResponseFuture future, HttpRequest request) {
        this.future = future;
        this.request = request;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        if(ctx.channel().isActive() && ctx.channel().isWritable()){
            //发送HTTP_STAGE_SENDING_EVENT事件（向前传递）
            ctx.fireUserEventTriggered(HTTP_STAGE_SENDING_EVENT);
            ChannelFuture writeFuture = ctx.write(request);
            writeFuture.addListener(f -> {
                ctx.fireUserEventTriggered(HTTP_STAGE_WAITING_EVENT);
            });
            ctx.flush();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.setAll(msg.headers());
        ByteBuf byteBuf = msg.content();
        msg.status();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, bytes);
        HttpResponse httpResponse = new HttpResponse.Builder()
                .httpHeaders(headers)
                .httpResponseBody(new HttpResponseBody(bytes))
                .status(msg.status().code())
                .message(msg.status().toString())
                .build();
        future.setSuccess(httpResponse);
        //HTTP_STAGE_COMPLETED_EVENT事件（向后传递）
        ctx.fireUserEventTriggered(HttpStageEvent.HTTP_STAGE_COMPLETED_EVENT);
        //一次Http会话结束，移除当前Handler对象
        ctx.pipeline().remove(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        future.setFailure(cause);
        ctx.close();
    }
}
