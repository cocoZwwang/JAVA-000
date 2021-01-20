package pers.cocoadel.cmq.core.message;


public interface CmqMessage<T> {
    T getBody();

    void addHeader(String key,Object value);

    Object getHeader(String key);

    CmqMessageHeaders getHeaders();

    void setHeaders(CmqMessageHeaders headers);
}
