package pers.cocoadel.cmq.client.netty;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Data
public class NettyClientProducer implements CmqProducer {

    private NettyCmqClient nettyCmqClient;

    private static final long TIME_OUT = 5000;

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        Describe describe = new Describe();
        describe.setTopic(topic);
        SendTextRequestBody requestBody = new SendTextRequestBody(describe);
        String json = JSON.toJSONString(message.getBody());
        GenericCmqMessage<String> jsonMsg = new GenericCmqMessage<>(message.getHeaders(), json);
        requestBody.setBody(jsonMsg);
        RequestFuture future = nettyCmqClient.sendMessage(requestBody, OperationType.SEND_MESSAGE);
        try {
            StreamResponse response = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
            return response.isSuccessful();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("send msg fail: " + Throwables.getStackTraceAsString(e));
        }
        return false;
    }
}
