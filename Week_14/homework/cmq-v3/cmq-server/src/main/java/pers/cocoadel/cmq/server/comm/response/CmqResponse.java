package pers.cocoadel.cmq.server.comm.response;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;
import pers.cocoadel.cmq.server.comm.enums.ResponseStatus;

import java.nio.charset.StandardCharsets;

@Data
@ToString
public class CmqResponse<T extends ResponseBody> {

    private Integer resultCode;

    private String resultMessage;

    private Long streamId;

    private Integer operationType;

    private T responseBody;

    public void encode(ByteBuf buf) {
        buf.writeLong(streamId);
        buf.writeInt(operationType);
        if (responseBody != null) {
            String json = JSON.toJSONString(responseBody);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            buf.writeBytes(bytes);
        }
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        resultCode = responseStatus.getCode();
        resultMessage = responseStatus.getMessage();
    }
}
