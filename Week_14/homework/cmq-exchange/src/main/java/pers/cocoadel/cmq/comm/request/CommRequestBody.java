package pers.cocoadel.cmq.comm.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.exception.CmqOperationException;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CommRequestBody extends RequestBody {

    private String topic;

    private String groupId;


    public CommRequestBody(String token, String topic, String groupId) {
        super(token);
        this.topic = topic;
        this.groupId = groupId;
    }

    public boolean check() {
        String checkResult = "OK";
        if (StringIsNull(getToken())) {
            checkResult = "token is empty!";
        } else if (topic == null || topic.isEmpty() || "null".equals(topic)) {
            checkResult = "topic is empty!";
        }
        if ("OK".equals(checkResult)) {
            return true;
        }
        CmqOperationException exception = CmqOperationException.createCmqOperationException(ResponseStatus.REQUEST_ERROR);
        exception.setMessage(checkResult);
        throw exception;
    }

    private boolean StringIsNull(String text) {
        return text == null || text.isEmpty() || "null".equalsIgnoreCase(text);
    }

}
