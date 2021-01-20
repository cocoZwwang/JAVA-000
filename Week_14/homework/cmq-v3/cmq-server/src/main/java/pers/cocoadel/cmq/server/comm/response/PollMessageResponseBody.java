package pers.cocoadel.cmq.server.comm.response;

import lombok.Data;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.util.List;

@Data
public class PollMessageResponseBody implements ResponseBody {
    private String topic;

    private String groupId;

    private List<GenericCmqMessage<String>> cmqMessages;
}

