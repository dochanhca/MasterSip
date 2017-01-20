package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ducpv on 1/19/17.
 */

public class GalleryItem implements Parcelable {

    private int nextId;
    private int totalImage;
    private List<ImageItem> imageItems;

    public GalleryItem() {
        this.nextId = 0;
    }

    public GalleryItem(int nextId) {
        this.nextId = nextId;
    }

    public GalleryItem(Parcel in) {
        nextId = in.readInt();
        imageItems = in.createTypedArrayList(ImageItem.CREATOR);
    }

    public static final Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel in) {
            return new GalleryItem(in);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
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
