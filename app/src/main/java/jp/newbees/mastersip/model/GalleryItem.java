package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ducpv on 1/19/17.
 */

public class GalleryItem implements Parcelable {

    private String nextId;
    private int totalImage;
    private List<ImageItem> imageItems;

    public GalleryItem() {
        this.nextId = null;
    }

    public GalleryItem(String nextId) {
        this.nextId = nextId;
    }

    public GalleryItem(Parcel in) {
        nextId = in.readString();
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

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

    public List<ImageItem> getPhotos() {
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
        parcel.writeString(nextId);
        parcel.writeTypedList(imageItems);
        parcel.writeInt(totalImage);
    }

    public boolean hasMorePhotos() {
        return nextId.equalsIgnoreCase("") ? false : true;
    }
}
