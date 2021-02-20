package pers.cocoadel.cmq.server.netty.exchange;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;
import pers.cocoadel.cmq.server.netty.exchange.support.Exchange;


/**
 * 远程消费者 和 本地消费者 交易
 */
@Slf4j
public class ExchangeConsumerHandler extends AbstractExchangeHandler {

    private final ServerExchangeCmqConsumer exchangeCmqConsumer;

    public ExchangeConsumerHandler(ServerExchangeCmqConsumer exchangeCmqConsumer) {
        super(ImmutableSet.of(
                OperationType.POLL_MESSAGE,
                OperationType.COMMIT,
                OperationType.SUBSCRIBE));
        this.exchangeCmqConsumer = exchangeCmqConsumer;
    }

    @Override
    public void handle(Exchange exchange) {
        StreamRequest<?> request = exchange.getStreamRequest();
        StreamResponse response = exchange.getStreamResponse();
        OperationType operationType = request.getOperationType();
        ConsumerRequestBody requestBody = (ConsumerRequestBody) request.getBody();
        //拉取消息
        if (operationType == OperationType.POLL_MESSAGE) {
            doPoll((PollRequestBody) requestBody, response);
        } else if (operationType == OperationType.COMMIT) {
            //提交确认
            doCommit(requestBody, response);
        } else if (operationType == OperationType.SUBSCRIBE) {
            //订阅
            doSubscribe(requestBody, response);
        }
        response.setResponseStatus(ResponseStatus.OK);
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
}
