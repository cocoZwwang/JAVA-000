package pers.cocoadel.learning.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;


public class HttpResponse {
    private HttpHeaders httpHeaders;

    private int status = HttpResponseStatus.OK.code();

    private String message;

    private HttpResponseBody httpResponseBody;

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }

    private HttpResponse() {
    }

    public static class Builder {
        private HttpHeaders httpHeaders;

        private HttpResponseBody httpResponseBody;

        private String message;

        private int status;

        public HttpResponse build() {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.httpHeaders = httpHeaders;
            httpResponse.httpResponseBody = httpResponseBody;
            httpResponse.status = status;
            httpResponse.message = message;
            return httpResponse;
        }

        public Builder httpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder httpResponseBody(HttpResponseBody httpResponseBody) {
            this.httpResponseBody = httpResponseBody;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder status(int status){
            this.status = status;
            return this;
        }
    }

}
