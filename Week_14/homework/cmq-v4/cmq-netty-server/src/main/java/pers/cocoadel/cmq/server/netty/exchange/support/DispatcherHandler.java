package pers.cocoadel.cmq.server.netty.exchange.support;

import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;
import pers.cocoadel.cmq.server.netty.exchange.ExchangeHandler;

import java.util.NoSuchElementException;

@Slf4j
public class DispatcherHandler extends SimpleChannelInboundHandler<StreamRequest<?>> {

    private final ExchangeHandlerRegistry handlerRegistry;

    public DispatcherHandler(ExchangeHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamRequest<?> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            ExchangeHandler exchangeHandler = handlerRegistry.get(msg.getOperationType());
            if (exchangeHandler == null) {
                throw new NoSuchElementException(String.format("OperationType{%s} is not match handler",
                        msg.getOperationType().getCode()));
            }
            Exchange exchange = new Exchange();
            exchange.setStreamRequest(msg);
            exchange.setStreamResponse(streamResponse);
            exchangeHandler.handle(exchange);
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
            log.error("DispatcherHandler: " + Throwables.getStackTraceAsString(e));
        } finally {
            ChannelUtil.writeAndFlushMessage(ctx, streamResponse);
        }
    }
}
