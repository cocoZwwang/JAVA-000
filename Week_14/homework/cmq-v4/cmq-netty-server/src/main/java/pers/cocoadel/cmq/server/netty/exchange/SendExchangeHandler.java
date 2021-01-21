package pers.cocoadel.cmq.server.netty.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.exchange.ExchangeCmqProducer;
import pers.cocoadel.cmq.server.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.server.netty.comm.StreamRequest;
import pers.cocoadel.cmq.server.netty.comm.StreamResponse;

@Slf4j
public class SendExchangeHandler extends SimpleChannelInboundHandler<StreamRequest<SendTextRequestBody>> {

    private final ExchangeCmqProducer producer;

    public SendExchangeHandler(ExchangeCmqProducer producer) {
        this.producer = producer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamRequest<SendTextRequestBody> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            SendTextRequestBody requestBody = msg.getBody();
            producer.send(requestBody);
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
        }finally {
            ChannelUtil.writeAndFlushMessage(ctx, streamResponse);
        }
    }
}
