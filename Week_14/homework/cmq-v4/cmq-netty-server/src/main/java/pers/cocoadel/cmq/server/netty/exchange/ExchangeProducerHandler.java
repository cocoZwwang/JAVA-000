package pers.cocoadel.cmq.server.netty.exchange;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.exchange.ExchangeCmqProducer;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;
import pers.cocoadel.cmq.server.netty.exchange.support.Exchange;

@Slf4j
public class ExchangeProducerHandler extends AbstractExchangeHandler {

    private final ExchangeCmqProducer producer;

    public ExchangeProducerHandler(ExchangeCmqProducer producer) {
        super(ImmutableSet.of(OperationType.SEND_MESSAGE));
        this.producer = producer;
    }

    @Override
    public void handle(Exchange exchange) {
        StreamRequest<?> request = exchange.getStreamRequest();
        StreamResponse response = exchange.getStreamResponse();
        SendTextRequestBody requestBody = (SendTextRequestBody) request.getBody();
        producer.send(requestBody);
        response.setResponseStatus(ResponseStatus.OK);
    }
}
