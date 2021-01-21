package pers.cocoadel.cmq.comm.enums;



import pers.cocoadel.cmq.comm.request.AuthRequestBody;
import pers.cocoadel.cmq.comm.request.PollRequestBody;
import pers.cocoadel.cmq.comm.request.RequestBody;
import pers.cocoadel.cmq.comm.request.SendTextRequestBody;
import pers.cocoadel.cmq.comm.response.AuthResponseBody;
import pers.cocoadel.cmq.comm.response.PollResponseBody;
import pers.cocoadel.cmq.comm.response.ResponseBody;

import java.util.stream.Stream;

public enum OperationType {
    AUTH(0, AuthRequestBody.class, AuthResponseBody.class),
    POLL_MESSAGE(1, PollRequestBody.class, PollResponseBody.class),
    SEND_MESSAGE(2, SendTextRequestBody.class, null);

    private final int code;

    private final Class<? extends RequestBody> requestBodyClass;

    private final Class<? extends ResponseBody> responseBodyClass;

    OperationType(int code, Class<? extends RequestBody> requestBodyClass,
                  Class<? extends ResponseBody> responseBodyClass) {
        this.code = code;
        this.requestBodyClass = requestBodyClass;
        this.responseBodyClass = responseBodyClass;
    }

    public int getCode() {
        return code;
    }

    public Class<? extends RequestBody> getRequestBodyClass() {
        return requestBodyClass;
    }

    public Class<? extends ResponseBody> getResponseBodyClass() {
        return responseBodyClass;
    }

    public static OperationType findOperationType(int code) {
        return Stream
                .of(values())
                .filter(operationType -> operationType.getCode() == code)
                .findFirst()
                .orElse(null);
    }
}
