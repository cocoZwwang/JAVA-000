package pers.cocoadel.cmq.server.comm.request;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import pers.cocoadel.cmq.server.comm.enums.OperationType;

import java.nio.charset.StandardCharsets;

@Data
public class CmqRequest<T extends RequestBody> {
    private Long streamId;

    private Integer operationType;

    private RequestBody requestBody;

    public void decode(ByteBuf buf) {
        streamId = buf.readLong();
        operationType = buf.readInt();
        OperationType type = OperationType.findOperationType(operationType);
        if (type == null) {
            throw new IllegalArgumentException(String.format("operationType{%s} is not support",operationType));
        }

        //这里不要使用 buf.array()，如果内存池使用的是直接内存会报错。
        int len = buf.readableBytes();
        if(len > 0){
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            requestBody = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), type.getRequestBodyClass());
        }
    }

}
