package pers.cocoade.learning.dubbo.api.enums;

public enum AccountState {
    NORMAL(0),
    FROZEN(1);
    private int code;

    AccountState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
