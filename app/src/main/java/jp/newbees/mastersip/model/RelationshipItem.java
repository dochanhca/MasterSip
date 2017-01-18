package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 12/20/16.
 */

public class RelationshipItem implements Parcelable {
    // followed
    public static final int FOLLOW = 1;
    public static final int UN_FOLLOW = 0;

    // online notification
    public static final int REGISTER = 1;
    public static final int UN_REGISTER = 0;

    private int isFollowed;
    private int isNotification;

    protected RelationshipItem(Parcel in) {
        isFollowed = in.readInt();
        isNotification = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isFollowed);
        dest.writeInt(isNotification);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RelationshipItem> CREATOR = new Creator<RelationshipItem>() {
        @Override
        public RelationshipItem createFromParcel(Parcel in) {
            return new RelationshipItem(in);
        }

        @Override
        public RelationshipItem[] newArray(int size) {
            return new RelationshipItem[size];
        }
    };

    public int isFollowed() {
        return isFollowed;
    }

    public void setFollowed(int followed) {
        isFollowed = followed;
    }

    public int getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(int isNotification) {
        this.isNotification = isNotification;
    }

    public RelationshipItem() {
    }
}
