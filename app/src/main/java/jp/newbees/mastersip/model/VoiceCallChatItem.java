package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 3/15/17.
 */

public class VoiceCallChatItem extends BaseChatItem implements Parcelable {

    private int kindCall;
    private String duration;

    public VoiceCallChatItem() {

    }

    protected VoiceCallChatItem(Parcel in) {
        super(in);
        kindCall = in.readInt();
        duration = in.readString();
    }

    public static final Creator<VoiceCallChatItem> CREATOR = new Creator<VoiceCallChatItem>() {
        @Override
        public VoiceCallChatItem createFromParcel(Parcel in) {
            return new VoiceCallChatItem(in);
        }

        @Override
        public VoiceCallChatItem[] newArray(int size) {
            return new VoiceCallChatItem[size];
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
