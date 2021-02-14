package pers.cocoadel.cmq.server.netty.exchange;

import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.exchange.ExchangeCmqProducer;
import pers.cocoadel.cmq.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

@Slf4j
public class ExchangeProducerHandler extends ExchangeHandler<StreamRequest<?>> {

    private final ExchangeCmqProducer producer;

    public ExchangeProducerHandler(ExchangeCmqProducer producer) {
        this.producer = producer;
    }

    @Override
    public void doChannelRead0(ChannelHandlerContext ctx, StreamRequest<?> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            SendTextRequestBody requestBody = (SendTextRequestBody) msg.getBody();
            producer.send(requestBody);
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
            log.error("ExchangeProducerHandler: " + Throwables.getStackTraceAsString(e));
        } finally {
            ChannelUtil.writeAndFlushMessage(ctx, streamResponse);
        }
    }

    @Override
    public boolean isMatch(StreamRequest<?> msg) {
        return msg != null && msg.getOperationType() == OperationType.SEND_MESSAGE;
    }
}
