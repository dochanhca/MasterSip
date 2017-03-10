package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/11/17.
 */

public class CallEvent {

    private int callType;
    private String roomId;

    public CallEvent(int callType, String roomId) {
        this.callType = callType;
        this.roomId = roomId;
    }

    public int getCallType() {
        return callType;
    }

    public String getRoomId() {
        return roomId;
    }
}
