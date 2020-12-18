package pers.cocoade.learning.dubbo.api.enums;

public enum  TransactionState {
    COMPLETED(0),TRADING(1),FAIL(2);

    private int code;

    TransactionState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
