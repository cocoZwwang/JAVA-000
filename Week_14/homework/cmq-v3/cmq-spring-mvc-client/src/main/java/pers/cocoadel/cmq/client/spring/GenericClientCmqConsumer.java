package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.util.CollectionUtils;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
public class GenericClientCmqConsumer<T> implements CmqConsumer<T> {

    private Class<T> tClass;

    private String url;

    private HttpDescribe httpDescribe;

    @Override
    public boolean subscribe(String topic) {
        ConsumerRequestBody requestBody = new ConsumerRequestBody(httpDescribe);
        return doCommRequestBody(requestBody, url + "/subscribe");
    }

    @Override
    public CmqMessage<T> pollNow() {
        List<CmqMessage<T>> list = pollNow(1);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<CmqMessage<T>> pollNow(int count) {
        PollRequestBody requestBody = new PollRequestBody(httpDescribe,count);
        try {
            Response response = HttpClientUtil.post(requestBody, url + "/poll");
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body().string();
                PollResponseBody body = JSON.parseObject(content, PollResponseBody.class);
                if (body.getCmqMessages() == null) {
                    return Collections.emptyList();
                }
                return body.getCmqMessages()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(message -> {
                            String json = message.getBody();
                            return new GenericCmqMessage<>(message.getHeaders(), JSON.parseObject(json, tClass));
                        })
                        .collect(Collectors.toList());
            } else {
                log.error(response.message() + ":" + Objects.requireNonNull(response.body()).string());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean commit() {
        ConsumerRequestBody requestBody = new ConsumerRequestBody(httpDescribe);
        return doCommRequestBody(requestBody, url + "/commit");
    }

    private boolean doCommRequestBody(ConsumerRequestBody requestBody, String address) {
        try {
            Response response = HttpClientUtil.post(requestBody, address);
            if (response.isSuccessful()) {
                return true;
            }
            log.error(response.message() + ":" + Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {

    }
}