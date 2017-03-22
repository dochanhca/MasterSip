package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/10/17.
 */

public class ReceivingCallEvent {
    public static final int INCOMING_CALL = 1;
    public static final int OUTGOING_CALL = 2;
    public static final int END_CALL = 13;
    public static final int INCOMING_CONNECTED_CALL = 6;
    public static final int OUTGOING_CONNECTED_CALL = 600;
    public static final int STREAMING_CALL = 7;
    public static final int RELEASE_CALL = 18;

    private int callEvent;
    private String callId;

    public ReceivingCallEvent(int callEvent) {
        this.callEvent = callEvent;
    }

    public ReceivingCallEvent(int callEvent, String callId) {
        this.callEvent = callEvent;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public int getCallEvent() {
        return callEvent;
    }
}
