package jp.newbees.mastersip.model;

import android.os.Parcel;

/**
 * Created by ducpv on 1/23/17.
 */

public class ImageChatItem extends BaseChatItem {

    private ImageItem imageItem;

    public ImageChatItem() {

    }

    protected ImageChatItem(Parcel in) {
        super(in);
        imageItem = in.readParcelable(ImageItem.class.getClassLoader());
    }

    public static final Creator<ImageChatItem> CREATOR = new Creator<ImageChatItem>() {
        @Override
        public ImageChatItem createFromParcel(Parcel in) {
            return new ImageChatItem(in);
        }

        @Override
        public ImageChatItem[] newArray(int size) {
            return new ImageChatItem[size];
        }
    };

    public ImageItem getImageItem() {
        return imageItem;
    }

    public void setImageItem(ImageItem imageItem) {
        this.imageItem = imageItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(imageItem, i);
    }
}
