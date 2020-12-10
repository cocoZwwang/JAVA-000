package pers.cocoadel.learning.hmily.order.enums;


public enum OrderState {
    START(0),
    SUCCESS(1),
    Filed(2);
    private final int state;

    OrderState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
