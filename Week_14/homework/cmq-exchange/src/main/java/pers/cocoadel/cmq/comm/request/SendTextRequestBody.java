package pers.cocoadel.cmq.comm.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.exception.CmqOperationException;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SendTextRequestBody extends CommRequestBody {

    private GenericCmqMessage<String> body;

    public SendTextRequestBody(String token, String topic, String groupId) {
        super(token, topic, groupId);
    }

    @Override
    public boolean check() {
        super.check();
        if (body == null) {
            CmqOperationException exception =
                    CmqOperationException.createCmqOperationException(ResponseStatus.REQUEST_ERROR);
            exception.setMessage("body is null");
            throw exception;
        }
        return true;
    }
}
