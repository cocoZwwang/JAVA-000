package pers.cocoadel.cmq.client.netty.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelConnectedHandler extends ChannelDuplexHandler {

    private boolean isActive = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            isActive = true;
            log.error("the channel is Active");
        } else {
            log.error("connect fail! the channel is unActive");
            ctx.close();
        }
        ctx.pipeline().remove(this);
    }

    public boolean isActive() {
        return isActive;
    }
}
