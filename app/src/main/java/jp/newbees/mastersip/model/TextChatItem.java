package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 1/4/17.
 */

public class TextChatItem extends BaseChatItem implements Parcelable{

    private String message;

    public TextChatItem (String message, int roomType, UserItem sender, UserItem sendee) {
        super(roomType,sender,sendee);
        this.message = message;
    }

    public TextChatItem(String message) {
        this.message = message;
    }

//    public TextChatItem(String message, String extensionSender) {
//        this(message);
//        this.setOwner(genUserItemFromExtension(extensionSender));
//    }
//
//    private  final UserItem genUserItemFromExtension(String extension){
//        UserItem userItem = new UserItem();
//        SipItem sipItem = new SipItem(extension);
//        userItem.setSipItem(sipItem);
//        return userItem;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.message);
    }

    protected TextChatItem(Parcel in) {
        super(in);
        this.message = in.readString();
    }

    public static final Creator<TextChatItem> CREATOR = new Creator<TextChatItem>() {
        @Override
        public TextChatItem createFromParcel(Parcel source) {
            return new TextChatItem(source);
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
