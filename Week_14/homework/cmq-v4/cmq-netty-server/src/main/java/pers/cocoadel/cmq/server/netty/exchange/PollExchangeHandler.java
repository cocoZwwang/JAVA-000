package pers.cocoadel.cmq.server.netty.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.server.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.server.netty.comm.StreamRequest;
import pers.cocoadel.cmq.server.netty.comm.StreamResponse;

/**
 * 远程消费者 和 本地消费者 交易
 */
@Slf4j
public class PollExchangeHandler extends SimpleChannelInboundHandler<StreamRequest<PollRequestBody>> {

    private final ServerExchangeCmqConsumer exchangeCmqConsumer;

    public PollExchangeHandler(ServerExchangeCmqConsumer exchangeCmqConsumer) {
        this.exchangeCmqConsumer = exchangeCmqConsumer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamRequest<PollRequestBody> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            PollResponseBody pollResponseBody = exchangeCmqConsumer.poll(msg.getBody());
            streamResponse.setBody(pollResponseBody);
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
        }finally {
            ChannelUtil.writeAndFlushMessage(ctx,streamResponse);
        }
    }
}
