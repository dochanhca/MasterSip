package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class ImageItem implements Parcelable, Serializable {

    public static final int IMAGE_PENDING = 0;
    public static final int IMAGE_APPROVED = 1;

    private int messageId;
    private int imageId;
    private String thumbUrl;
    private String originUrl;
    private int imageType;
    private int imageStatus;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getImageStatus() {
        return imageStatus;
    }

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

    /**
     * Default constructor
     */
    public ImageItem() {
        this.imageId = 0;
        this.thumbUrl = "";
        this.originUrl = "";
    }

    public ImageItem(String thumbUrl, String originUrl) {
        this.thumbUrl = thumbUrl;
        this.originUrl = originUrl;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public boolean isApproved() {
        return this.imageStatus == ImageItem.IMAGE_APPROVED ? true : false;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getImageType() {
        return imageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.messageId);
        dest.writeInt(this.imageId);
        dest.writeString(this.thumbUrl);
        dest.writeString(this.originUrl);
        dest.writeInt(this.imageType);
        dest.writeInt(this.imageStatus);
        dest.writeInt(this.height);
        dest.writeInt(this.width);

    }

    protected ImageItem(Parcel in) {
        this.messageId = in.readInt();
        this.imageId = in.readInt();
        this.thumbUrl = in.readString();
        this.originUrl = in.readString();
        this.imageType = in.readInt();
        this.imageStatus = in.readInt();
        this.height = in.readInt();
        this.width = in.readInt();
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
