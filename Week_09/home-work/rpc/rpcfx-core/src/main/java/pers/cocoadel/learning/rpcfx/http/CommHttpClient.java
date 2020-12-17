package pers.cocoadel.learning.rpcfx.http;

import io.netty.handler.codec.http.HttpMethod;
import pers.cocoadel.learning.http.HttpRequest;
import pers.cocoadel.learning.http.HttpResponse;
import pers.cocoadel.learning.http.NettyHttpClient;

import java.nio.charset.StandardCharsets;

public class CommHttpClient {

    private static final NettyHttpClient NETTY_HTTP_CLIENT = new NettyHttpClient();

    public static HttpResponse postByJson(String url,String json){
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .content(json.getBytes(StandardCharsets.UTF_8))
                .httpMethod(HttpMethod.POST)
                .build();
        return NETTY_HTTP_CLIENT.executor(request);
    }
}
