package jp.newbees.mastersip.event;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 4/5/17.
 */

public class GSMCallEvent {
    public static final int PAUSED_GSM_CALL_EVENT = 1;
    public static final int RESUME_GSM_CALL_EVENT = 2;

    private final int callEvent;
    private UserItem user;

    public GSMCallEvent(UserItem user, int callEvent) {
        this.callEvent = callEvent;
        this.user = user;
    }

    public UserItem getUser() {
        return user;
    }

    public int getCallEvent() {
        return callEvent;
    }
}
