package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 3/15/17.
 */

public class CallChatItem extends BaseChatItem implements Parcelable {

    private int kindCall;
    private String duration;

    public CallChatItem() {

    }

    protected CallChatItem(Parcel in) {
        super(in);
        kindCall = in.readInt();
        duration = in.readString();
    }

    public static final Creator<CallChatItem> CREATOR = new Creator<CallChatItem>() {
        @Override
        public CallChatItem createFromParcel(Parcel in) {
            return new CallChatItem(in);
        }

        @Override
        public CallChatItem[] newArray(int size) {
            return new CallChatItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(kindCall);
        parcel.writeString(duration);
    }

    public int getKindCall() {
        return kindCall;
    }

    public void setKindCall(int kindCall) {
        this.kindCall = kindCall;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
