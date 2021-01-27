package pers.cocoadel.cmq.netty.comm;

import lombok.Data;

@Data
public class StreamRequest<T> {
    //请求流 序列号，用来实现管线化请求
    private Long streamId;
    //操作类型
    private OperationType operationType;
    //请求体
    private T body;

    public static  <E> StreamRequest<E> createStreamRequestWithoutBody(StreamRequest<?> src) {
        StreamRequest<E> request = new StreamRequest<>();
        request.streamId = src.getStreamId();
        request.operationType = src.getOperationType();
        return request;
    }
}
