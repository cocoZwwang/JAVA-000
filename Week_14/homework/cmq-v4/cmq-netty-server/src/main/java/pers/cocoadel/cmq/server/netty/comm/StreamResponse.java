package pers.cocoadel.cmq.server.netty.comm;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;

import java.nio.charset.StandardCharsets;

@Data
public class StreamResponse {
    private Long streamId;

    private OperationType operationType;

    private Integer resultCode;

    private String resultMessage;

    private Object body;

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

    public void encode(ByteBuf buf) {
        buf.writeLong(streamId);
        buf.writeInt(operationType.getCode());
        if (body != null) {
            String json = JSON.toJSONString(body);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            buf.writeBytes(bytes);
        }
    }
}
