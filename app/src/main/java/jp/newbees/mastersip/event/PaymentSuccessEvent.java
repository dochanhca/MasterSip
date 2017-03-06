package jp.newbees.mastersip.event;

/**
 * Created by ducpv on 3/4/17.
 */

public class PaymentSuccessEvent {
    private String point;

    public PaymentSuccessEvent(String point) {
        this.point = point;
    }

    public String getPoint() {
        return point;
    }
}
