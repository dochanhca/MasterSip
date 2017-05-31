package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 3/29/17.
 */

public class FCMPushItem implements Parcelable {

    private String badge;
    private String userName;
    private String message;
    private String sound;
    private String linkUrl;
    private String category;

    public static final Creator<FCMPushItem> CREATOR = new Creator<FCMPushItem>() {
        @Override
        public FCMPushItem createFromParcel(Parcel in) {
            return new FCMPushItem(in);
        }

        @Override
        public FCMPushItem[] newArray(int size) {
            return new FCMPushItem[size];
        }
    };

    public FCMPushItem() {

    }

    protected FCMPushItem(Parcel in) {
        badge = in.readString();
        userName = in.readString();
        message = in.readString();
        sound = in.readString();
        linkUrl = in.readString();
        category = in.readString();
    }

    public static final class CATEGORY {
        public static final String INCOMING_CALL = "INCOMING_CALL";
        public static final String MISS_CALL = "MISS_CALL";
        public static final String CHAT_TEXT = "CHAT_TEXT";
        public static final String FOLLOW = "FOLLOW";
        public static final String USER_ONLINE = "USER_ONLINE";
        public static final String COMMON_PUSH = "COMMON_PUSH";
        public static final String NOTIFY = "NOTIFY";
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(badge);
        dest.writeString(userName);
        dest.writeString(message);
        dest.writeString(sound);
        dest.writeString(linkUrl);
        dest.writeString(category);
    }
}
