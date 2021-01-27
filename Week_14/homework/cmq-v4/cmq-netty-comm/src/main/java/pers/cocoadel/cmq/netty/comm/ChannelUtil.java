package pers.cocoadel.cmq.netty.comm;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelUtil {

    public static void writeAndFlushMessage(ChannelHandlerContext ctx, Object msg) {
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(msg);
        } else {
            //如果当前通过不可，暂时丢弃然后打印日志
            log.error("dropped cmqResponse: {}", msg);
        }
    }
}
