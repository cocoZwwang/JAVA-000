package per.cocoadel.learning.subscrib;


public class DeliverProductEvent {
    private Long orderId;

    private String message;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
