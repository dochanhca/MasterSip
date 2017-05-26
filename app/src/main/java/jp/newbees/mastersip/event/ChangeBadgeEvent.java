package jp.newbees.mastersip.event;

/**
 * Created by vynv on 5/18/17.
 */

public class ChangeBadgeEvent {
    private int type;
    private String badge;

    public ChangeBadgeEvent(int type, String badge) {
        this.type = type;
        this.badge = badge;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
