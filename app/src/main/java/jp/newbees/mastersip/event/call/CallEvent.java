package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/11/17.
 */

public class CallEvent {

    private int callType;
    private String callee;

    public CallEvent(int callType, String callee) {
        this.callType = callType;
        this.callee = callee;
    }

    public int getCallType() {
        return callType;
    }

    public String getCallee() {
        return callee;
    }
}
