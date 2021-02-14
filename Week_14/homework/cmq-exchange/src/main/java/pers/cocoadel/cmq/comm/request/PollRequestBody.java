package pers.cocoadel.cmq.comm.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.core.message.Describe;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 拉取消息请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PollRequestBody extends ConsumerRequestBody {
    private int messageCount;

    public PollRequestBody(Describe describe,int messageCount) {
        super(describe);
        this.messageCount = messageCount;
    }

    @Override
    public void check(){
        super.check();
        checkArgument(messageCount > 0,"messageCount must be > 0");
    }

}
