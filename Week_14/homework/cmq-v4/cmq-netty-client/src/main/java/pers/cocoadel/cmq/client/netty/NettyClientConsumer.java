package pers.cocoadel.cmq.client.netty;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Data
@Slf4j
public class NettyClientConsumer<T> implements CmqConsumer<T> {

    private NettyCmqClient nettyCmqClient;

    private Describe describe;

    private Class<T> tClass;

    private final static long TIME_OUT = 5000;

    public NettyClientConsumer() {

    }

    @Override
    public boolean subscribe(String topic) {
        try {
            ConsumerRequestBody consumerRequestBody = new ConsumerRequestBody(describe);
            RequestFuture future = nettyCmqClient.sendMessage(consumerRequestBody, OperationType.SUBSCRIBE);
            StreamResponse response = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
            return response.isSuccessful();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CmqMessage<T> pollNow() {
        List<CmqMessage<T>> list = pollNow(1);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<CmqMessage<T>> pollNow(int count) {
        try {
            PollRequestBody requestBody = new PollRequestBody(describe, 1);
            RequestFuture future = nettyCmqClient.sendMessage(requestBody,OperationType.POLL_MESSAGE);
            StreamResponse response = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
            String content = response.getBody().toString();
            return parseResponse(content);
        } catch (Exception e) {
            log.info("poll Now error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<CmqMessage<T>> parseResponse(String content) {
        PollResponseBody body = JSON.parseObject(content, PollResponseBody.class);
        if (body.getCmqMessages() == null) {
            return Collections.emptyList();
        }
        return body.getCmqMessages()
                .stream()
                .filter(Objects::nonNull)
                .map(jsonMessage -> {
                    String json = jsonMessage.getBody();
                    return new GenericCmqMessage<>(jsonMessage.getHeaders(), JSON.parseObject(json, tClass));
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean commit() {
        try {
            ConsumerRequestBody requestBody = new ConsumerRequestBody(describe);
            RequestFuture future = nettyCmqClient.sendMessage(requestBody,OperationType.COMMIT);
            StreamResponse response;
            response = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
            return response.isSuccessful();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }
}
