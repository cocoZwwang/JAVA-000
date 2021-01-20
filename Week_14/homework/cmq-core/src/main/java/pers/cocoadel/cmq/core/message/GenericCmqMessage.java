package pers.cocoadel.cmq.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
public class GenericCmqMessage<T> implements CmqMessage<T> {
    private CmqMessageHeaders cmqMessageHeaders = new CmqMessageHeaders();

    private T body;

    @Override
    public void addHeader(String key, Object value) {
        cmqMessageHeaders.put(key,value);
    }

    @Override
    public Object getHeader(String key) {
        return cmqMessageHeaders.get(key);
    }

    @Override
    public CmqMessageHeaders getHeaders() {
        return cmqMessageHeaders;
    }

    @Override
    public void setHeaders(CmqMessageHeaders headers) {
        cmqMessageHeaders = headers;
    }
}
