package pers.cocoadel.cmq.comm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.comm.request.ConsumerRequestBody;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollResponseBody extends ConsumerResponseBody {

    public PollResponseBody(String topic, String groupId,String consumerName) {
        super(topic, groupId,consumerName);
    }

    private List<GenericCmqMessage<String>> cmqMessages;

    public static PollResponseBody createPollResponseBody(ConsumerRequestBody requestBody) {
        PollResponseBody responseBody = new PollResponseBody();
        Describe describe = requestBody.getDescribe();
        responseBody.setTopic(describe.getTopic());
        responseBody.setGroupId(describe.getGroupId());
        responseBody.setConsumerName(describe.getName());
        return responseBody;
    }
}

