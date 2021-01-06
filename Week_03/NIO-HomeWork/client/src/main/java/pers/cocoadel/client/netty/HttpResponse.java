package pers.cocoadel.client.netty;

import io.netty.handler.codec.http.HttpHeaders;


public class HttpResponse {
    private HttpHeaders httpHeaders;

    private byte[] body;

    public HttpResponse(HttpHeaders httpHeaders, byte[] body) {
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
