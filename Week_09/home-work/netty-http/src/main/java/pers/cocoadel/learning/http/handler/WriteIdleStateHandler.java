package pers.cocoadel.learning.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WriteIdleStateHandler extends IdleStateHandler {
    public WriteIdleStateHandler(long keepaliveTime) {
        super(0, keepaliveTime, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        //如果接收到${keepaliveTime}秒没有写数据的idle事件，关闭通道。
        if(evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT){
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
