package jp.newbees.mastersip.eventbus;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 1/13/17.
 */
public class SendingReadMessageEvent {
    private BaseChatItem baseChatItem;
    private UserItem currentUser;
    private UserItem replyUser;

    public SendingReadMessageEvent(BaseChatItem baseChatItem, UserItem currentUser, UserItem replyUser) {
        this.baseChatItem = baseChatItem;
        this.currentUser = currentUser;
        this.replyUser = replyUser;
    }

    public BaseChatItem getBaseChatItem() {
        return baseChatItem;
    }

    public void setBaseChatItem(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }

    public UserItem getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserItem currentUser) {
        this.currentUser = currentUser;
    }

    public UserItem getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(UserItem replyUser) {
        this.replyUser = replyUser;
    }
}