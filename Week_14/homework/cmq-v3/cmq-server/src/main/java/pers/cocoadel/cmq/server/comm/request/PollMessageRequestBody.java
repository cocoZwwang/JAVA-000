package pers.cocoadel.cmq.server.comm.request;

import lombok.Data;

/**
 * 拉取消息请求
 */
@Data
public class PollMessageRequestBody implements RequestBody {
//    private int messageCount;

    private String topic;

    private String groupId;

    private String checkResult;

    public boolean check() {
        if (topic == null || topic.isEmpty() || "null".equals(topic)) {
            checkResult = "topic is empty!";
            return false;
        }
        checkResult = "OK";
        return true;
    }
}
