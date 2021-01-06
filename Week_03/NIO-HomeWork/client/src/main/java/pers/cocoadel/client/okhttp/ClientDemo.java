package pers.cocoadel.client.okhttp;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ClientDemo {

    /**
     * 网关访问测试
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        String url = "http://localhost:8801/";
        String url = "https://www.baidu.com/";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        for(int i = 0; i < 10;i++){

            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.body().string());
        }
    }
}
