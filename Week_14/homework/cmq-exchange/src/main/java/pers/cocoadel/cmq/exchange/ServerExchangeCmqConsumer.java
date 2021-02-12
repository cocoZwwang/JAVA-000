package pers.cocoadel.cmq.exchange;

import pers.cocoadel.cmq.comm.exception.CmqOperationException;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.util.Collections;
import java.util.List;

public class ServerExchangeCmqConsumer extends ExchangeCmqConsumer<String> {

    @Override
    public void subscribe(ConsumerRequestBody requestBody) {
        requestBody.check();
        try {
            Describe describe = requestBody.getDescribe();
            cmqBroker.createTopic(describe.getTopic());
            CmqConsumer<String> consumer = createConsumer(describe);
            consumer.subscribe(describe.getTopic());
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
        Describe describe = requestBody.getDescribe();
        CmqConsumer<String> consumer = createConsumer(describe);
        CmqMessage<String> cmqMessage = consumer.pollNow();
        if(cmqMessage == null){
            return null;
        }
        GenericCmqMessage<String> genericCmqMessage = (GenericCmqMessage<String>) cmqMessage;
        return Collections.singletonList(genericCmqMessage);
    }

    @Override
    public void commit(ConsumerRequestBody requestBody) {
        requestBody.check();
        //执行commit
        try {
            CmqConsumer<String> consumer = createConsumer(requestBody.getDescribe());
            consumer.commit();
        } catch (Exception e) {
            throw CmqOperationException.createServerErrorException(e);
        }
    }

}
