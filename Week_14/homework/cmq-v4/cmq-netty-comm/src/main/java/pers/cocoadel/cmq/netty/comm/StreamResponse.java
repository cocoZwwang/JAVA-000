package pers.cocoadel.cmq.netty.comm;

import lombok.Data;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;

@Data
public class StreamResponse {
    private Long streamId;

    private OperationType operationType;

    private Integer resultCode;

    private String resultMessage;

    private Object body;

    public boolean isSuccessful() {
        return resultCode == ResponseStatus.OK.getCode();
    }

    public static StreamResponse createStreamResponse(StreamRequest<?> request) {
        StreamResponse response = new StreamResponse();
        response.setStreamId(request.getStreamId());
        response.setOperationType(request.getOperationType());
        return response;
    }

    public static StreamResponse createStreamResponse(StreamRequest<?> request, ResponseStatus status) {
        StreamResponse response = new StreamResponse();
        response.setStreamId(request.getStreamId());
        response.setOperationType(request.getOperationType());
        response.setResultCode(status.getCode());
        response.setResultMessage(status.getMessage());
        return response;
    }

//    public void encode(ByteBuf buf) {
//        buf.writeLong(streamId);
//        buf.writeInt(operationType.getCode());
//        buf.writeInt(resultCode);
//        if (Strings.isNullOrEmpty(resultMessage)) {
//            buf.writeInt(0);
//        } else {
//            byte[] resultMsg = resultMessage.getBytes(StandardCharsets.UTF_8);
//            buf.writeInt(resultMsg.length);
//            buf.writeBytes(resultMsg);
//        }
//        if (body != null) {
//            byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
//            buf.writeBytes(bytes);
//        }
//    }
//
//    public void decode(ByteBuf buf) {
//        setStreamId(buf.readLong());
//        int type = buf.readInt();
//        OperationType operationType = OperationType.findOperationType(type);
//        setOperationType(operationType);
//        if (operationType == null) {
//            throw new IllegalArgumentException(String.format("operationType{%s} is not support",type));
//        }
//        // 返回码
//        setResultCode(buf.readInt());
//        // resultMsg 长度
//        int resultMsgLen = buf.readInt();
//        if (resultMsgLen > 0) {
//            byte[] bytes = new byte[resultMsgLen];
//            buf.readBytes(bytes);
//            setResultMessage(new String(bytes, StandardCharsets.UTF_8));
//        }
//
//        //这里不要使用 buf.array()，如果内存池使用的是直接内存会报错。
//        int len = buf.readableBytes();
//        if(len > 0){
//            byte[] body = new byte[len];
//            buf.readBytes(body);
//            setBody(body);
//        }
//    }
}
