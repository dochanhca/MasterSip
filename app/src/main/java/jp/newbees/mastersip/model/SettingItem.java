package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 1/18/17.
 */

public class SettingItem implements Parcelable {

    public static final int ON = 1;
    public static final int OFF = 0;

    private int voiceCall;
    private int videoCall;
    private int chat;

    public SettingItem() {
    }

    protected SettingItem(Parcel in) {
        voiceCall = in.readInt();
        videoCall = in.readInt();
        chat = in.readInt();
    }

    public static final Creator<SettingItem> CREATOR = new Creator<SettingItem>() {
        @Override
        public SettingItem createFromParcel(Parcel in) {
            return new SettingItem(in);
        }

        @Override
        public SettingItem[] newArray(int size) {
            return new SettingItem[size];
        }
    };

    public int getVoiceCall() {
        return voiceCall;
    }

    public void setVoiceCall(int voiceCall) {
        this.voiceCall = voiceCall;
    }

    public int getVideoCall() {
        return videoCall;
    }

    public void setVideoCall(int videoCall) {
        this.videoCall = videoCall;
    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(voiceCall);
        parcel.writeInt(videoCall);
        parcel.writeInt(chat);
    }
}
