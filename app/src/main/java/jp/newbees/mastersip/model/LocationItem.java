package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */
public class LocationItem implements Serializable, Parcelable {
    private int id;
    private String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
    }

    public LocationItem() {
    }

    protected LocationItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<LocationItem> CREATOR = new Parcelable.Creator<LocationItem>() {
        @Override
        public LocationItem createFromParcel(Parcel source) {
            return new LocationItem(source);
        }

        @Override
        public LocationItem[] newArray(int size) {
            return new LocationItem[size];
        }
    };
}
