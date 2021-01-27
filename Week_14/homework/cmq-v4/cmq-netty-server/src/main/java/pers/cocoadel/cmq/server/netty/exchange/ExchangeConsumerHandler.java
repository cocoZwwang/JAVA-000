package pers.cocoadel.cmq.server.netty.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

/**
 * 远程消费者 和 本地消费者 交易
 */
@Slf4j
public class ExchangeConsumerHandler extends SimpleChannelInboundHandler<StreamRequest<? extends CommRequestBody>> {

    private final ServerExchangeCmqConsumer exchangeCmqConsumer;

    public ExchangeConsumerHandler(ServerExchangeCmqConsumer exchangeCmqConsumer) {
        this.exchangeCmqConsumer = exchangeCmqConsumer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamRequest<? extends CommRequestBody> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            OperationType operationType = streamResponse.getOperationType();
            CommRequestBody requestBody = msg.getBody();
            //拉取消息
            if (operationType == OperationType.POLL_MESSAGE) {
                doPoll((PollRequestBody) requestBody, streamResponse);
            } else if (operationType == OperationType.COMMIT) {
                //提交确认
                doCommit(requestBody, streamResponse);
            } else if (operationType == OperationType.SUBSCRIBE) {
                //订阅
                doSubscribe(requestBody,streamResponse);
            }
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
        } finally {
            ChannelUtil.writeAndFlushMessage(ctx, streamResponse);
        }
    }

    private void doPoll(PollRequestBody requestBody, StreamResponse streamResponse) {
        PollResponseBody pollResponseBody = exchangeCmqConsumer.poll(requestBody);
        streamResponse.setBody(pollResponseBody);
    }

    private void doSubscribe(CommRequestBody request, StreamResponse streamResponse) {
        exchangeCmqConsumer.subscribe(request);
    }

    private void doCommit(CommRequestBody request, StreamResponse streamResponse) {
        exchangeCmqConsumer.commit(request);
    }

}
