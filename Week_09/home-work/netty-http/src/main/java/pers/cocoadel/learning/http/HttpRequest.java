package pers.cocoadel.learning.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpRequest {

    private String url;

    private HttpMethod httpMethod;

    private byte[] content;

    private HttpHeaders httpHeaders;
}
