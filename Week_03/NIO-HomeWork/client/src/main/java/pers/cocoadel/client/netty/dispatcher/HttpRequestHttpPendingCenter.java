package pers.cocoadel.client.netty.dispatcher;

import pers.cocoadel.client.netty.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestHttpPendingCenter {
    public static final String HEADER_STREAM_ID = "streamId";
    private final Map<String, HttpResponseFuture> map = new ConcurrentHashMap<>();

    public void add(String requestId,HttpResponseFuture responseFuture){
        map.put(requestId,responseFuture);
    }

    public void set(String requestId, HttpResponse response){
        if(map.containsKey(requestId)){
            HttpResponseFuture httpResponseFuture = map.get(requestId);
            httpResponseFuture.setSuccess(response);
            map.remove(requestId);
            System.out.println("返回成功，request ID ： " + requestId);
        }
    }
}
