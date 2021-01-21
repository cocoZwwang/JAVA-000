package pers.cocoadel.cmq.comm.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CmqOperationException extends RuntimeException {

    private int code;

    private String message;

    public static CmqOperationException createCmqOperationException(ResponseStatus responseStatus) {
        CmqOperationException exception = new CmqOperationException();
        exception.code = responseStatus.getCode();
        exception.message = responseStatus.getMessage();
        return exception;
    }

    public static CmqOperationException createServerErrorException(Exception e) {
        CmqOperationException exception = new CmqOperationException();
        exception.code = ResponseStatus.SERVER_ERROR.getCode();
        exception.message = e.getMessage();
        return exception;
    }
}
