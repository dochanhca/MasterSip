package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/11/17.
 */

public class CallEvent {

    private int callType;
    private String callId;

    public CallEvent(int callType, String callId) {
        this.callType = callType;
        this.callId = callId;
    }

    public int getCallType() {
        return callType;
    }

    public String getCallId() {
        return callId;
    }
}
