package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.message.CmqMessage;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.core.producer.CmqProducer;

import java.io.IOException;
import java.util.Objects;

@Data
@Slf4j
public class GenericClientCmqProducer implements CmqProducer {
    private String url;

    private final String path = "/send";

    private String token;

    private String getAddress() {
        return url + path;
    }

    @Override
    public boolean send(String topic, CmqMessage<?> message) {
        SendTextRequestBody requestBody = new SendTextRequestBody(token, topic, null);
        String json = JSON.toJSONString(message.getBody());
        GenericCmqMessage<String> jsonMsg = new GenericCmqMessage<>(message.getHeaders(), json);
        requestBody.setBody(jsonMsg);
        try {
            Response response = HttpClientUtil.post(requestBody, getAddress());
            if(response.isSuccessful()){
                return true;
            }
            log.error(response.message()+ ":" + Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public void setCmqBroker(CmqBroker broker) {
        throw new IllegalStateException("client no need set CmqBroker!");
    }

    public void close() {

    }
}
