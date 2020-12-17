package pers.cocoadel.learning.http.handler;

import io.netty.handler.codec.http.HttpHeaders;


public class HttpResponse {
    private HttpHeaders httpHeaders;

    private int status = 200;

    private byte[] body;

    public HttpResponse(HttpHeaders httpHeaders, byte[] body) {
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public HttpResponse(HttpHeaders httpHeaders,int status, byte[] body) {
        this.httpHeaders = httpHeaders;
        this.body = body;
        this.status = status;
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

    public int getStatus() {
        return status;
    }
}
