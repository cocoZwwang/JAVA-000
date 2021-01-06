package per.cocoadel.learning.demo.order;

public enum OrderState {
    NO_PAYED(0),
    PAYED(1),
    DELIVER_PRODUCT(2),
    COMPLETED(3),
    FAILURE(4);
    private final int code;

    OrderState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
