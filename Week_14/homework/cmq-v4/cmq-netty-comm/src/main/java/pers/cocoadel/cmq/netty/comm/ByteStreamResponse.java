package pers.cocoadel.cmq.netty.comm;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;

@EqualsAndHashCode(callSuper = true)
@Data
public class ByteStreamResponse extends StreamResponse {

    private byte[] bytes;

    public static ByteStreamResponse createByteStreamResponse(StreamResponse src) {
        ByteStreamResponse response = new ByteStreamResponse();
        response.setResultMessage(src.getResultMessage());
        response.setStreamId(src.getStreamId());
        response.setResultCode(src.getResultCode());
        response.setOperationType(src.getOperationType());
        response.setBody(src.getBody());
        return response;
    }

    public void encode(ByteBuf buf) {
        buf.writeLong(getStreamId());
        buf.writeInt(getOperationType().getCode());
        buf.writeInt(getResultCode());
        if (Strings.isNullOrEmpty(getResultMessage())) {
            buf.writeInt(0);
        } else {
            byte[] resultMsg = getResultMessage().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(resultMsg.length);
            buf.writeBytes(resultMsg);
        }
        if (bytes != null && bytes.length > 0) {
            buf.writeBytes(bytes);
        }
    }

    public void decode(ByteBuf buf) {
        setStreamId(buf.readLong());
        int type = buf.readInt();
        OperationType operationType = OperationType.findOperationType(type);
        setOperationType(operationType);
        if (operationType == null) {
            throw new IllegalArgumentException(String.format("operationType{%s} is not support", type));
        }
        // 返回码
        setResultCode(buf.readInt());
        // resultMsg 长度
        int resultMsgLen = buf.readInt();
        if (resultMsgLen > 0) {
            byte[] msgBytes = new byte[resultMsgLen];
            buf.readBytes(msgBytes);
            setResultMessage(new String(msgBytes, StandardCharsets.UTF_8));
        }

        //这里不要使用 buf.array()，如果内存池使用的是直接内存会报错。
        int len = buf.readableBytes();
        if (len > 0) {
            bytes = new byte[len];
            buf.readBytes(bytes);
            setBody(bytes);
        }
    }
}
