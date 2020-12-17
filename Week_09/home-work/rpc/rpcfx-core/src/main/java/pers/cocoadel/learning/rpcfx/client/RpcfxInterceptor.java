package pers.cocoadel.learning.rpcfx.client;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import pers.cocoadel.learning.http.HttpResponse;
import pers.cocoadel.learning.rpcfx.api.RpcfxRequest;
import pers.cocoadel.learning.rpcfx.api.RpcfxResponse;
import pers.cocoadel.learning.rpcfx.http.CommHttpClient;

import java.lang.reflect.Method;

@Slf4j
public class RpcfxInterceptor implements MethodInterceptor {

    private final String url;

    private final Class<?> serviceClass;

    public RpcfxInterceptor(String url,Class<?> serviceClass) {
        this.url = url;
        this.serviceClass = serviceClass;
    }


    public Object getInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     * RPC客户端接口方法代理
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcfxRequest request = new RpcfxRequest();
        String serverName = serviceClass.getName();
        log.info("serverName: {}", serverName);
        request.setServiceClass(serverName);
        request.setMethod(method.getName());
        request.setParams(objects);
        RpcfxResponse response = post(request, url);
        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException
        System.out.println(response.getResult().toString());
        return JSON.parse(response.getResult().toString());
    }

    /**
     * 使用netty client发送Http请求
     */
    private RpcfxResponse post(RpcfxRequest req, String url) throws Exception {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: " + reqJson);
        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        HttpResponse httpResponse = CommHttpClient.postByJson(url,reqJson);
        String respJson = new String(httpResponse.getHttpResponseBody().getBody());
        System.out.println("resp json: " + respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }
}
