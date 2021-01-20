package pers.cocoadel.cmq.server.comm.request;

import lombok.Data;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

@Data
public class SendTextMessageRequestBody implements RequestBody {
    private String topic;

    private GenericCmqMessage<String> body;

    private String checkResult;


    public boolean check() {
        checkResult = "OK";
        if (topic == null || topic.isEmpty() || "null".equals(topic)) {
            checkResult = "topic is empty";
            return false;
        }

        if (body == null) {
            checkResult = "message body is empty";
            return false;
        }
        return true;
    }
}
