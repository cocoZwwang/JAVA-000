package pers.cocoadel.cmq.server.netty.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class ExchangeHandler<I> extends SimpleChannelInboundHandler<I> {

    public abstract boolean isMatch(I msg);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
        doChannelRead0(ctx, msg);
    }

    public abstract void doChannelRead0(ChannelHandlerContext ctx, I msg) throws Exception;
}
