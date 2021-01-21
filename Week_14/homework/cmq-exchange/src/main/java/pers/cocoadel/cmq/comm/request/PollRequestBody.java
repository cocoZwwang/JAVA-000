package pers.cocoadel.cmq.comm.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 拉取消息请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PollRequestBody extends CommRequestBody {
//    private int messageCount;

    public PollRequestBody(String token, String topic, String groupId) {
        super(token, topic, groupId);
    }

}
