package pers.cocoadel.cmq.server.netty.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.StreamRequest;

import java.util.List;

@Slf4j
public class DispatcherHandler extends SimpleChannelInboundHandler<StreamRequest<?>> {

    public List<ExchangeHandler<StreamRequest<?>>> getExchangeHandlers() {
        return exchangeHandlers;
    }

    public void setExchangeHandlers(List<ExchangeHandler<StreamRequest<?>>> exchangeHandlers) {
        this.exchangeHandlers = exchangeHandlers;
    }

    private List<ExchangeHandler<StreamRequest<?>>> exchangeHandlers;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamRequest<?> msg) throws Exception {
        boolean flag = false;
        for (ExchangeHandler<StreamRequest<?>> exchangeHandler : exchangeHandlers) {
            if (exchangeHandler.isMatch(msg)) {
                flag = true;
                log.info("DispatcherHandler request [{}] match ExChangeHandler: {}",
                        msg, exchangeHandler.getClass().getName());
                exchangeHandler.doChannelRead0(ctx, msg);
                break;
            }
        }

        if (!flag) {
            log.info("DispatcherHandler request [{}] is not match ExChangeHandler", msg);
        }
    }
}
