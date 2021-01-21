package pers.cocoadel.cmq.server.netty.comm;

import io.netty.buffer.ByteBuf;

public class ByteStreamRequest extends StreamRequest<byte[]> {

    public void decode(ByteBuf buf) {
        setStreamId(buf.readLong());
        int type = buf.readInt();
        OperationType operationType = OperationType.findOperationType(type);
        setOperationType(operationType);
        if (operationType == null) {
            throw new IllegalArgumentException(String.format("operationType{%s} is not support",type));
        }

        //这里不要使用 buf.array()，如果内存池使用的是直接内存会报错。
        int len = buf.readableBytes();
        if(len > 0){
            byte[] body = new byte[len];
            buf.readBytes(body);
            setBody(body);
        }
    }
}
