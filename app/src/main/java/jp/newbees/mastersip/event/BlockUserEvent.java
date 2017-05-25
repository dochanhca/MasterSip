package jp.newbees.mastersip.event;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by ducpv on 5/25/17.
 */

public class BlockUserEvent {

    UserItem userItem;

    public BlockUserEvent(UserItem userItem) {
        this.userItem = userItem;
    }

    public UserItem getUserItem() {
        return userItem;
    }
}
