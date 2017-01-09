package jp.newbees.mastersip.ui.top;

import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatItem extends BaseChatItem{
    private String message;
    private String time;
    private String avatar;
    private boolean isReply = false;

    public ChatItem(String message, String time, String avatar, boolean isReply) {
        this.message = message;
        this.time = time;
        this.avatar = avatar;
        this.isReply = isReply;
    }

    public ChatItem(String message, String time, String avatar) {
        this.message = message;
        this.time = time;
        this.avatar = avatar;
    }

    public ChatItem() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }
}
