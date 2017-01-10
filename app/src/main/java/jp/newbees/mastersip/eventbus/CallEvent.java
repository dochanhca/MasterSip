package jp.newbees.mastersip.eventbus;

/**
 * Created by vietbq on 1/10/17.
 */

public class CallEvent {
    public static final int ACTION_ACCEPT_CALL = 3;
    public static final int CANCEL_CALL = 4;
    public static final int INCOMING_CALL = 1;
    public static final int OUTGOING_CALL = 2;
    public static final int BUSY_CALL = 5;

    private int callEvent;
    private String callerExtension;

    public CallEvent(int callEvent) {
        this.callEvent = callEvent;
    }

    public CallEvent(int callEvent, String callerExtension) {
        this.callEvent = callEvent;
        this.callerExtension = callerExtension;
    }

    public String getCallerExtension() {
        return callerExtension;
    }

    public int getCallEvent() {
        return callEvent;
    }
}
