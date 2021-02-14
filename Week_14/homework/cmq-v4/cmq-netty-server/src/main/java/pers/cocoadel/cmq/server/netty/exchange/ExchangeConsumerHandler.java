package pers.cocoadel.cmq.server.netty.exchange;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.netty.comm.ChannelUtil;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.Set;

/**
 * 远程消费者 和 本地消费者 交易
 */
@Slf4j
public class ExchangeConsumerHandler extends ExchangeHandler<StreamRequest<?>> {

    private final ServerExchangeCmqConsumer exchangeCmqConsumer;

    private final Set<OperationType> matchOperationTypeSet = ImmutableSet.of(
            OperationType.POLL_MESSAGE,
            OperationType.COMMIT,
            OperationType.SUBSCRIBE);

    public ExchangeConsumerHandler(ServerExchangeCmqConsumer exchangeCmqConsumer) {
        this.exchangeCmqConsumer = exchangeCmqConsumer;
    }

    @Override
    public void doChannelRead0(ChannelHandlerContext ctx, StreamRequest<?> msg) throws Exception {
        StreamResponse streamResponse = StreamResponse.createStreamResponse(msg);
        try {
            OperationType operationType = streamResponse.getOperationType();
            ConsumerRequestBody requestBody = (ConsumerRequestBody) msg.getBody();
            //拉取消息
            if (operationType == OperationType.POLL_MESSAGE) {
                doPoll((PollRequestBody) requestBody, streamResponse);
            } else if (operationType == OperationType.COMMIT) {
                //提交确认
                doCommit(requestBody, streamResponse);
            } else if (operationType == OperationType.SUBSCRIBE) {
                //订阅
                doSubscribe(requestBody, streamResponse);
            }
            streamResponse.setResultCode(ResponseStatus.OK.getCode());
            streamResponse.setResultMessage(ResponseStatus.OK.getMessage());
        } catch (Exception e) {
            streamResponse.setResultCode(ResponseStatus.SERVER_ERROR.getCode());
            streamResponse.setResultMessage(e.getMessage());
            log.error("ExchangeConsumerHandler: " + Throwables.getStackTraceAsString(e));
        } finally {
            ChannelUtil.writeAndFlushMessage(ctx, streamResponse);
        }
    }

    private void doPoll(PollRequestBody requestBody, StreamResponse streamResponse) {
        PollResponseBody pollResponseBody = exchangeCmqConsumer.poll(requestBody);
        streamResponse.setBody(pollResponseBody);
    }

    private void doSubscribe(ConsumerRequestBody request, StreamResponse streamResponse) {
        exchangeCmqConsumer.subscribe(request);
    }

    private void doCommit(ConsumerRequestBody request, StreamResponse streamResponse) {
        exchangeCmqConsumer.commit(request);
    }

    @Override
    public boolean isMatch(StreamRequest<?> msg) {
        if (msg == null) {
            return false;
        }
        return matchOperationTypeSet.contains(msg.getOperationType());
    }

}
