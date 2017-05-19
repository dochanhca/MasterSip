package jp.newbees.mastersip.event;

import android.support.annotation.NonNull;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 5/16/17.
 */

public class FooterDialogEvent implements Comparable<FooterDialogEvent> {
    private long id;
    private int type;
    private UserItem competitor;
    private String message = "";
    private int iconResourceId;
    private int giftPoint;

    public FooterDialogEvent(int type, UserItem competitor) {
        id = System.currentTimeMillis();
        this.type = type;
        this.competitor = competitor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserItem getCompetitor() {
        return competitor;
    }

    public void setCompetitor(UserItem competitor) {
        this.competitor = competitor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public int getGiftPoint() {
        return giftPoint;
    }

    public void setGiftPoint(int giftPoint) {
        this.giftPoint = giftPoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NonNull FooterDialogEvent event) {
        return Long.compare(event.getId(), id);
    }
}
