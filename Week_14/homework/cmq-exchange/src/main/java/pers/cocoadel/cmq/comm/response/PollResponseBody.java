package pers.cocoadel.cmq.comm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollResponseBody extends CommResponseBody {

    public PollResponseBody(String topic, String groupId) {
        super(topic, groupId);
    }

    private List<GenericCmqMessage<String>> cmqMessages;

    public static PollResponseBody createPollResponseBody(CommRequestBody requestBody) {
        PollResponseBody responseBody = new PollResponseBody();
        responseBody.setTopic(requestBody.getTopic());
        responseBody.setGroupId(requestBody.getGroupId());
        return responseBody;
    }
}

