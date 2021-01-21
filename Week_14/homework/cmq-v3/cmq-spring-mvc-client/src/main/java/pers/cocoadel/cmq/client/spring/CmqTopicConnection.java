package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import okhttp3.Response;
import org.springframework.util.StringUtils;
import pers.cocoadel.cmq.comm.request.AuthRequestBody;
import pers.cocoadel.cmq.comm.response.AuthResponseBody;
import pers.cocoadel.cmq.connection.Connection;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.exchange.ConsumerKey;
import pers.cocoadel.cmq.exchange.ProducerKey;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CmqTopicConnection implements Connection {

    private final Map<ConsumerKey, GenericClientCmqConsumer<?>> consumerMap = new ConcurrentHashMap<>();

    private final Map<ProducerKey, GenericClientCmqProducer> producerMap = new ConcurrentHashMap<>();


    private String url;

    private String token;

    public CmqTopicConnection() {

    }

    @SuppressWarnings("unchecked")
    public <T> CmqConsumer<T> createConsumer(String topic, String groupId, Class<T> tClass) {
        if (StringUtils.isEmpty(token)) {
            throw new IllegalStateException("the connection is not connected");
        }
        if (StringUtils.isEmpty(topic)) {
            throw new NullPointerException("the topic is null");
        }
        ConsumerKey consumerKey = new ConsumerKey(token, topic, groupId);
        return (CmqConsumer<T>) consumerMap.computeIfAbsent(consumerKey, k -> {
            GenericClientCmqConsumer<T> genericClientCmqConsumer = new GenericClientCmqConsumer<>();
            genericClientCmqConsumer.setTopic(topic);
            genericClientCmqConsumer.setUrl(url);
            genericClientCmqConsumer.setTClass(tClass);
            genericClientCmqConsumer.setToken(token);
            return genericClientCmqConsumer;
        });
    }

    public CmqProducer createProducer() {
        if (StringUtils.isEmpty(token)) {
            throw new IllegalStateException("the connection is not connected");
        }
        ProducerKey producerKey = new ProducerKey(token);
        return producerMap.computeIfAbsent(producerKey, k -> {
            GenericClientCmqProducer producer = new GenericClientCmqProducer();
            producer.setToken(token);
            producer.setUrl(url);
            return producer;
        });
    }

    @Override
    public void connect(String ip, int port) throws IOException {
        AuthRequestBody requestBody = new AuthRequestBody();
        requestBody.setName("admin");
        requestBody.setPassword("123456");
        try {
            String address = "http://" + ip + ":" + port + "/connect";
            Response response = HttpClientUtil.post(requestBody, address);
            IOException exception = new IOException(response.isSuccessful() ? "connect fail" : response.message());
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                AuthResponseBody responseBody = JSON.parseObject(json, AuthResponseBody.class);
                token = responseBody.getToken();
                url = "http://" + ip + ":" + port;
                return;
            }
            throw exception;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void disConnect() throws IOException {
        AuthRequestBody requestBody = new AuthRequestBody();
        requestBody.setToken(token);
        HttpClientUtil.post(requestBody, url + "/disconnect");
        consumerMap.values().forEach(GenericClientCmqConsumer::close);
        producerMap.values().forEach(GenericClientCmqProducer::close);
        consumerMap.clear();
        producerMap.clear();
    }
}
