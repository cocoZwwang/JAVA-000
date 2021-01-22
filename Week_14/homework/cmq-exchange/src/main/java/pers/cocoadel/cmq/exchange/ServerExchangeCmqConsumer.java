package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.exception.CmqOperationException;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.util.Collections;
import java.util.List;

public class ServerExchangeCmqConsumer extends ExchangeCmqConsumer<String> {

    @Override
    public void subscribe(CommRequestBody requestBody) {
        requestBody.check();
        try {
            cmqBroker.createTopic(requestBody.getTopic());
            CmqConsumer<String> consumer = createConsumer(requestBody.getTopic(),requestBody.getToken());
            consumer.subscribe(requestBody.getTopic());
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

    @Override
    public PollResponseBody poll(PollRequestBody requestBody) {
        requestBody.check();
        try {
            //拉取消息
            List<GenericCmqMessage<String>> list = doPoll(requestBody);
            //构建 PollResponseBody
            PollResponseBody responseBody = PollResponseBody.createPollResponseBody(requestBody);
            responseBody.setCmqMessages(list);
            return responseBody;
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

    private List<GenericCmqMessage<String>> doPoll(PollRequestBody requestBody) {
        // todo 暂时先一次啦一条消息
        // todo 暂时还没处理 group id
        CmqConsumer<String> consumer = createConsumer(requestBody.getTopic(),requestBody.getToken());
        CmqMessage<String> cmqMessage = consumer.poll();
        if(cmqMessage == null){
            return null;
        }
        GenericCmqMessage<String> genericCmqMessage = (GenericCmqMessage<String>) cmqMessage;
        return Collections.singletonList(genericCmqMessage);
    }

    @Override
    public void commit(CommRequestBody requestBody) {
        requestBody.check();
        //执行commit
        try {
            CmqConsumer<String> consumer = createConsumer(requestBody.getTopic(),requestBody.getToken());
            consumer.commit();
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

}
