package jp.newbees.mastersip.model;

import android.os.Parcel;

/**
 * Created by vietbq on 1/4/17.
 */

public class TextChatItem extends BaseChatItem {

    private String message;

    /**
     * @param message
     * @param roomType
     * @param sender
     * @param sendee
     */
    public TextChatItem (String message, int roomType, UserItem sender, UserItem sendee) {
        super(roomType,sender,sendee);
        this.message = message;
    }

    /**
     * @param message
     */
    public TextChatItem(String message) {
        this.message = message;
    }

    /**
     * Default constructor
     */
    public TextChatItem() {
    }

    protected TextChatItem(Parcel in) {
        super(in);
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextChatItem> CREATOR = new Creator<TextChatItem>() {
        @Override
        public TextChatItem createFromParcel(Parcel in) {
            return new TextChatItem(in);
        }

        @Override
        public TextChatItem[] newArray(int size) {
            return new TextChatItem[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
