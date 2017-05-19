package jp.newbees.mastersip.event;

/**
 * Created by vynv on 5/18/17.
 */

public class ChangeBadgeEvent {
    private int type;
    private int badge;

    public ChangeBadgeEvent(int type, int badge) {
        this.type = type;
        this.badge = badge;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }
}
