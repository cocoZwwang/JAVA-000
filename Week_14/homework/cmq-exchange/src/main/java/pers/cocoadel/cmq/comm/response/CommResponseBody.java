package pers.cocoadel.cmq.comm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.comm.request.CommRequestBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommResponseBody implements ResponseBody {

    private String topic;

    private String groupId;

    public static CommResponseBody createCommResponseBody(CommRequestBody requestBody) {
        CommResponseBody responseBody = new CommResponseBody();
        responseBody.topic = requestBody.getTopic();
        responseBody.groupId = requestBody.getGroupId();
        return responseBody;
    }
}
