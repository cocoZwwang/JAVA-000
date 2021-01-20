package pers.cocoadel.cmq.server.comm.enums;

public enum ResponseStatus {
    OK(200,"OK"),
    REQUEST_ERROR(400,"request error"),
    SERVER_ERROR(500,"server Error");
    private final int code;

    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
