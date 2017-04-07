package jp.newbees.mastersip.event;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 4/5/17.
 */

public class GSMCallEvent {
    private final int callEvent;
    private UserItem user;

    public final static int PAUSED_GSM_CALL_EVENT = 1;
    public final static int RESUME_GSM_CALL_EVENT = 2;

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
