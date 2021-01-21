package pers.cocoadel.cmq.client.spring;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import java.io.IOException;

public class HttpClientUtil {

    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    public static Response post(pers.cocoadel.cmq.comm.request.RequestBody req, String url) throws IOException {
        String reqJson = JSON.toJSONString(req);
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        return client.newCall(request).execute();
    }
}
