package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 12/20/16.
 */

public class RelationshipItem implements Parcelable {
    private int isFollowed;
    private boolean isNotification;

    public int isFollowed() {
        return isFollowed;
    }

    public void setFollowed(int followed) {
        isFollowed = followed;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public RelationshipItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isFollowed);
        dest.writeByte(this.isNotification ? (byte) 1 : (byte) 0);
    }

    protected RelationshipItem(Parcel in) {
        this.isFollowed = in.readInt();
        this.isNotification = in.readByte() != 0;
    }

    public static final Creator<RelationshipItem> CREATOR = new Creator<RelationshipItem>() {
        @Override
        public RelationshipItem createFromParcel(Parcel source) {
            return new RelationshipItem(source);
        }

        @Override
        public RelationshipItem[] newArray(int size) {
            return new RelationshipItem[size];
        }
    };
}
