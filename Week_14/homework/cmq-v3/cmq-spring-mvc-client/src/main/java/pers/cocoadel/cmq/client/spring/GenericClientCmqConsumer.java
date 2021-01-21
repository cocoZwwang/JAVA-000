package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.comm.response.ResponseBody;
import pers.cocoadel.cmq.core.consumer.CmqConsumerSupport;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class GenericClientCmqConsumer<T> extends CmqConsumerSupport<T> {

    private String topic;

    private String groupId;

    private Class<T> tClass;

    private String url;

    private String token;

    @Override
    public boolean subscribe(String topic) {
        CommRequestBody requestBody = new CommRequestBody(token, topic, groupId);
        return doCommRequestBody(requestBody, url + "/subscribe");
    }

    @Override
    public boolean commit() {
        CommRequestBody requestBody = new CommRequestBody(token, topic, groupId);
        return doCommRequestBody(requestBody, url + "/commit");
    }

    private boolean doCommRequestBody(CommRequestBody requestBody, String address) {
        try {
            Response response = HttpClientUtil.post(requestBody, address);
            if (response.isSuccessful()) {
                return true;
            }
            log.error(response.message()+ ":" + Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public CmqMessage<T> poll() {
        PollRequestBody requestBody = new PollRequestBody(token,topic, groupId);
        try {
            Response response = HttpClientUtil.post(requestBody, url + "/poll");
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body().string();
                PollResponseBody body = JSON.parseObject(content, PollResponseBody.class);
                List<GenericCmqMessage<String>> messageList = body.getCmqMessages()
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (messageList.size() > 0) {
                    GenericCmqMessage<String> jsonMessage = messageList.get(0);
                    String json = jsonMessage.getBody();
                    return new GenericCmqMessage<>(jsonMessage.getHeaders(), JSON.parseObject(json, tClass));
                }
            }else {
                log.error(response.message()+ ":" + Objects.requireNonNull(response.body()).string());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public void close() {

    }
}