package pers.cocoadel.client.okhttp;


import okhttp3.*;
import okhttp3.internal.connection.RealConnectionPool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClientDemo {

    /**
     * 网关访问测试
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        String url = "http://localhost:8801/";
        String url = "https://www.baidu.com/";
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectionPool(new ConnectionPool(10,10, TimeUnit.MINUTES))
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        for(int i = 0; i < 10;i++){

            final Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.body().string());
        }
    }
}
