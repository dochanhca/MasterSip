package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class AvailableTimeItem implements Parcelable, Serializable {
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

    public AvailableTimeItem() {
    }

    protected AvailableTimeItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<AvailableTimeItem> CREATOR = new Parcelable.Creator<AvailableTimeItem>() {
        @Override
        public AvailableTimeItem createFromParcel(Parcel source) {
            return new AvailableTimeItem(source);
        }

        @Override
        public AvailableTimeItem[] newArray(int size) {
            return new AvailableTimeItem[size];
        }
    };
}
