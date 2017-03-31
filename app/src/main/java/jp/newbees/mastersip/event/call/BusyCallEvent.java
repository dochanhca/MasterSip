package jp.newbees.mastersip.event.call;

/**
 * Created by ducpv on 3/9/17.
 */

public class BusyCallEvent {

    private String callId;

    public BusyCallEvent() {
    }

    public BusyCallEvent(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }
}
