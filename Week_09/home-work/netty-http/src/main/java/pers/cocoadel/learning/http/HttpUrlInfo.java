package pers.cocoadel.learning.http;

import io.netty.handler.codec.http.HttpScheme;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HttpUrlInfo {
    private String host;

    private int port;

    private String path;

    private String url;

    private String scheme;

    public static HttpUrlInfo parse(String url){
        if(!url.startsWith(HttpScheme.HTTP.name().toString()) &&
                url.startsWith(HttpScheme.HTTPS.name().toString())){
            throw new IllegalArgumentException(String.format("the url(%s) format error!",url));
        }

        HttpUrlInfo urlInfo = new HttpUrlInfo();
        urlInfo.url = url;
        int index = 0;
        if(url.startsWith(HttpScheme.HTTPS.name().toString())){
            urlInfo.scheme = HttpScheme.HTTPS.name().toString();
            urlInfo.port = HttpScheme.HTTPS.port();
            index = HttpScheme.HTTPS.name().toString().length() + 3;
        }else{
            urlInfo.scheme = HttpScheme.HTTP.name().toString();
            urlInfo.port = HttpScheme.HTTP.port();
            index = HttpScheme.HTTP.name().toString().length() + 3;
        }

        url = url.substring(index);
        index = url.indexOf("/");
        String address = url;
        urlInfo.path  = "/";
        if (index != -1) {
            address = url.substring(0,index);
            urlInfo.path  = url.substring(index);

        }

        index = address.indexOf(":");
        if(index != -1){
            urlInfo.host = address.substring(0, index);
            index = index + 1;
            urlInfo.port = Integer.parseInt(address.substring(index));
        }else{
            urlInfo.host = address;
        }
        return urlInfo;
    }
}
