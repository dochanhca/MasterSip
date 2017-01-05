package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 1/4/17.
 */

public class DeletedChatItem extends BaseChatItem implements Parcelable{
    private String message;

    /**
     * Default constructor
     */
    public DeletedChatItem() {
        //Nothing
    }

    protected DeletedChatItem(Parcel in) {
        super(in);
        this.message = in.readString();
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.message);
    }



    public static final Creator<DeletedChatItem> CREATOR = new Creator<DeletedChatItem>() {
        @Override
        public DeletedChatItem createFromParcel(Parcel source) {
            return new DeletedChatItem(source);
        }

        @Override
        public DeletedChatItem[] newArray(int size) {
            return new DeletedChatItem[size];
        }
    };
}
