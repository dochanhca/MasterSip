package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/10/17.
 */

public class SendingCallEvent {
    public static final int ACCEPT_CALL = 1;
    public static final int END_CALL = 2;
    public static final int REJECT_CALL = 3;

    private int event;

    public SendingCallEvent(int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }

}
