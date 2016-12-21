package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class UserItem implements Serializable, Parcelable {

    public static final int FEMALE = 0;
    public static final int MALE = 1;


    @NonNull
    private String username;

    @NonNull
    private SelectionItem location;
    private SelectionItem jobItem;
    private SelectionItem availableTimeItem;
    private SelectionItem typeGirl;
    private String typeBoy;

    public String getTypeBoy() {
        return typeBoy;
    }

    public void setTypeBoy(String typeBoy) {
        this.typeBoy = typeBoy;
    }

    private String charmingPoint;
    private String memo;
    private int gender;
    private String dateOfBirth;
    private int coin;
    private int status;
    @Nullable
    private String email;

    @Nullable
    private ImageItem avatarItem;

    @Nullable
    private String avatarUrl;
    @Nullable
    private String facebookId;
    @NonNull
    private SipItem sipItem;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public UserItem() {
        this.jobItem = new SelectionItem();
        this.typeGirl = new SelectionItem();
        this.availableTimeItem = new SelectionItem();
        this.memo = "";
        this.typeBoy = "";
        this.charmingPoint = "";
        this.avatarItem = new ImageItem();
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public SelectionItem getLocation() {
        return location;
    }

    public void setLocation(@NonNull SelectionItem location) {
        this.location = location;
    }

    public SelectionItem getJobItem() {
        return jobItem;
    }

    public void setJobItem(SelectionItem jobItem) {
        this.jobItem = jobItem;
    }

    public SelectionItem getAvailableTimeItem() {
        return availableTimeItem;
    }

    public void setAvailableTimeItem(SelectionItem availableTimeItem) {
        this.availableTimeItem = availableTimeItem;
    }

    public String getCharmingPoint() {
        return charmingPoint;
    }

    public void setCharmingPoint(String charmingPoint) {
        this.charmingPoint = charmingPoint;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public ImageItem getAvatarItem() {
        return avatarItem;
    }

    public void setAvatarItem(@Nullable ImageItem avatarItem) {
        this.avatarItem = avatarItem;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(@Nullable String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Nullable
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(@Nullable String facebookId) {
        this.facebookId = facebookId;
    }

    @NonNull
    public SipItem getSipItem() {
        return sipItem;
    }

    public void setSipItem(@NonNull SipItem sipItem) {
        this.sipItem = sipItem;
    }

    public SelectionItem getTypeGirl() {
        return typeGirl;
    }

    public void setTypeGirl(SelectionItem typeGirl) {
        this.typeGirl = typeGirl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.jobItem, flags);
        dest.writeParcelable(this.availableTimeItem, flags);
        dest.writeParcelable(this.typeGirl, flags);
        dest.writeString(this.typeBoy);
        dest.writeString(this.charmingPoint);
        dest.writeString(this.memo);
        dest.writeInt(this.gender);
        dest.writeString(this.dateOfBirth);
        dest.writeInt(this.coin);
        dest.writeInt(this.status);
        dest.writeString(this.email);
        dest.writeParcelable(this.avatarItem, flags);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.facebookId);
        dest.writeParcelable(this.sipItem, flags);
        dest.writeString(this.userId);
    }

    protected UserItem(Parcel in) {
        this.username = in.readString();
        this.location = in.readParcelable(SelectionItem.class.getClassLoader());
        this.jobItem = in.readParcelable(SelectionItem.class.getClassLoader());
        this.availableTimeItem = in.readParcelable(SelectionItem.class.getClassLoader());
        this.typeGirl = in.readParcelable(SelectionItem.class.getClassLoader());
        this.typeBoy = in.readString();
        this.charmingPoint = in.readString();
        this.memo = in.readString();
        this.gender = in.readInt();
        this.dateOfBirth = in.readString();
        this.coin = in.readInt();
        this.status = in.readInt();
        this.email = in.readString();
        this.avatarItem = in.readParcelable(ImageItem.class.getClassLoader());
        this.avatarUrl = in.readString();
        this.facebookId = in.readString();
        this.sipItem = in.readParcelable(SipItem.class.getClassLoader());
        this.userId = in.readString();
    }

    public static final Creator<UserItem> CREATOR = new Creator<UserItem>() {
        @Override
        public UserItem createFromParcel(Parcel source) {
            return new UserItem(source);
        }

        @Override
        public UserItem[] newArray(int size) {
            return new UserItem[size];
        }
    };
}
