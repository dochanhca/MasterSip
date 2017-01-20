package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class ImageItem implements Parcelable, Serializable {

    public static final int IMAGE_PENDING  = 0;
    public static final int IMAGE_APPROVED = 1;

    private int imageId;
    private String thumbUrl;
    private String originUrl;

    public int getImageStatus() {
        return imageStatus;
    }

    private int imageStatus;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public ImageItem() {
        this.imageId = 0;
        this.thumbUrl = "";
        this.originUrl = "";
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.imageId);
        dest.writeString(this.thumbUrl);
        dest.writeInt(this.imageStatus);
        dest.writeString(this.originUrl);
    }

    protected ImageItem(Parcel in) {
        this.imageId = in.readInt();
        this.thumbUrl = in.readString();
        this.imageStatus = in.readInt();
        this.originUrl = in.readString();
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
