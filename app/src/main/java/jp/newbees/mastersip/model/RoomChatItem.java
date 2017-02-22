package jp.newbees.mastersip.model;

/**
 * Created by vietbq on 2/2/17.
 */

public class RoomChatItem {
    private String roomId;
    private UserItem userChat;
    private String lastMessage;
    private String lastMessageTimeStamp;
    private int numberMessageUnRead;
    private boolean isShowingCheckbox;
    private boolean isSelected;

    public RoomChatItem() {
        this.isSelected = false;
        this.isShowingCheckbox = false;
    }

    public boolean isShowingCheckbox() {
        return isShowingCheckbox;
    }

    public void setShowingCheckbox(boolean showingCheckbox) {
        isShowingCheckbox = showingCheckbox;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setUserChat(UserItem userChat) {
        this.userChat = userChat;
    }

    public UserItem getUserChat() {
        return userChat;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessageTimeStamp(String lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setNumberMessageUnRead(int numberMessageUnRead) {
        this.numberMessageUnRead = numberMessageUnRead;
    }

    public int getNumberMessageUnRead() {
        return numberMessageUnRead;
    }
}
