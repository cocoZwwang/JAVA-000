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

    public void setResponseStatus(ResponseStatus responseStatus) {
        resultCode = responseStatus.getCode();
        resultMessage = responseStatus.getMessage();
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
}
