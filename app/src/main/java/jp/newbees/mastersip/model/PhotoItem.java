package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ducpv on 1/19/17.
 */

public class PhotoItem implements Parcelable {

    private int nextId;
    private int totalImage;
    private List<ImageItem> imageItems;

    public PhotoItem() {
    }

    protected PhotoItem(Parcel in) {
        nextId = in.readInt();
        imageItems = in.createTypedArrayList(ImageItem.CREATOR);
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(nextId);
        parcel.writeTypedList(imageItems);
        parcel.writeInt(totalImage);
    }
}
