package jp.newbees.mastersip.model;

import android.os.Parcel;

/**
 * Created by vietbq on 1/4/17.
 */

public class DeletedChatItem extends BaseChatItem {

    private String message;

    /**
     * Default constructor
     */
    public DeletedChatItem() {
        //Nothing
    }

    protected DeletedChatItem(Parcel in) {
        super(in);
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(message);
    }

    public static final Creator<DeletedChatItem> CREATOR = new Creator<DeletedChatItem>() {
        @Override
        public DeletedChatItem createFromParcel(Parcel in) {
            return new DeletedChatItem(in);
        }

        @Override
        public DeletedChatItem[] newArray(int size) {
            return new DeletedChatItem[size];
        }
    };

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
