package jp.newbees.mastersip.event.call;

import org.linphone.core.CallDirection;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 1/10/17.
 */

public class ReceivingCallEvent {
    public static final int INCOMING_CALL = 1;
    public static final int OUTGOING_CALL = 2;
    public static final int CHECKED_INCOMING_VOICE_CALL = 100;
    public static final int CHECKED_INCOMING_VIDEO_CALL = 101;
    public static final int CHECKED_INCOMING_VIDEO_CHAT_CALL = 102;
    public static final int INCOMING_CONNECTED_CALL = 6;
    public static final int STREAMS_RUNNING = 7;
    public static final int PAUSED_CALL = 9;
    public static final int RESUMING_CALL = 10;
    public static final int LINPHONE_ERROR = 12;
    public static final int END_CALL = 13;
    public static final int RELEASE_CALL = 18;
    public static final int OUTGOING_CONNECTED_CALL = 600;

    private int callEvent;
    private String callId;
    private UserItem callUser;
    private CallDirection direction;

    public ReceivingCallEvent(int callEvent) {
        this.callEvent = callEvent;
    }

    public ReceivingCallEvent(int callEvent,CallDirection direction, String callId) {
        this.callEvent = callEvent;
        this.callId = callId;
        this.direction = direction;
    }

    public ReceivingCallEvent(int callEvent, UserItem callUser, String callId) {
        this.callEvent = callEvent;
        this.callUser = callUser;
        this.callId = callId;
    }

    public ReceivingCallEvent(int callEvent, String callId) {
        this.callEvent = callEvent;
        this.callId = callId;
    }

    public UserItem getCallUser() {
        return callUser;
    }

    public String getCallId() {
        return callId;
    }

    public int getCallEvent() {
        return callEvent;
    }

    public CallDirection getDirection() {
        return direction;
    }
}
