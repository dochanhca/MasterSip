package jp.newbees.mastersip.event;

/**
 * Created by ducpv on 4/21/17.
 */

public class SettingOnlineChangedEvent {

    boolean isFollowing;
    private String userId;

    public SettingOnlineChangedEvent(boolean isFollowing, String userId) {
        this.isFollowing = isFollowing;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
