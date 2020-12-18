package pers.cocoadel.learning.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.ObjectUtil;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class HttpRequest {

    private String url;

    private HttpMethod httpMethod;

    private byte[] content;

    private HttpHeaders httpHeaders;

    private HttpUrlInfo httpUrlInfo;

    public HttpUrlInfo parseUrl(){
        if(httpUrlInfo != null && Objects.equals(httpUrlInfo.getUrl(),url)){
            return httpUrlInfo;
        }
        httpUrlInfo = HttpUrlInfo.parse(url);
        return httpUrlInfo;
    }


}
