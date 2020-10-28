package pers.cocoadel.http.client;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ClientDemo {


    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8081/";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        System.out.println(response.body().toString());
    }
}
