package pers.cocoadel.cmq.comm.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.core.message.Describe;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

import static com.google.common.base.Preconditions.checkNotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SendTextRequestBody extends ProducerRequestBody {

    private GenericCmqMessage<String> body;

    public SendTextRequestBody(Describe describe) {
        super(describe);
    }

    @Override
    public void check() {
        super.check();
        checkNotNull(body);
    }
}
