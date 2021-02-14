package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;

import java.io.IOException;
import java.util.Objects;

@Data
@Slf4j
public class GenericClientCmqProducer implements CmqProducer {
    private String url;

    private String path = "/send";

    private HttpDescribe httpDescribe;

    private String getAddress() {
        return url + path;
    }

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        Describe describe = httpDescribe.getDescribe();
        describe.setTopic(topic);
        SendTextRequestBody requestBody = new SendTextRequestBody(describe);
        String json = JSON.toJSONString(message.getBody());
        GenericCmqMessage<String> jsonMsg = new GenericCmqMessage<>(message.getHeaders(), json);
        requestBody.setBody(jsonMsg);
        try {
            Response response = HttpClientUtil.post(requestBody, getAddress());
            if (response.isSuccessful()) {
                return true;
            }
            log.error(response.message() + ":" + Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public void close() {

    }
}
