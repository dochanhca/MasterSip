package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class SipItem implements Parcelable , Serializable{
    public SipItem(String extension, String password) {
        this.extension = extension;
        this.secret = password;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private String extension;
    private String secret;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.extension);
        dest.writeString(this.secret);
    }

    public SipItem() {
    }

    protected SipItem(Parcel in) {
        this.extension = in.readString();
        this.secret = in.readString();
    }

    public static final Parcelable.Creator<SipItem> CREATOR = new Parcelable.Creator<SipItem>() {
        @Override
        public SipItem createFromParcel(Parcel source) {
            return new SipItem(source);
        }

        @Override
        public SipItem[] newArray(int size) {
            return new SipItem[size];
        }
    };
}
