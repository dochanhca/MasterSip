package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 2/2/17.
 */
public class GiftItem implements Parcelable {
    private int giftId;
    private String name;
    private ImageItem giftImage;
    private int price;

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGiftImage(ImageItem giftImage) {
        this.giftImage = giftImage;
    }

    public ImageItem getGiftImage() {
        return giftImage;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.giftId);
        dest.writeString(this.name);
        dest.writeParcelable(this.giftImage, flags);
        dest.writeInt(this.price);
    }

    public GiftItem() {
    }

    protected GiftItem(Parcel in) {
        this.giftId = in.readInt();
        this.name = in.readString();
        this.giftImage = in.readParcelable(ImageItem.class.getClassLoader());
        this.price = in.readInt();
    }

    public static final Parcelable.Creator<GiftItem> CREATOR = new Parcelable.Creator<GiftItem>() {
        @Override
        public GiftItem createFromParcel(Parcel source) {
            return new GiftItem(source);
        }

        @Override
        public GiftItem[] newArray(int size) {
            return new GiftItem[size];
        }
    };
}
