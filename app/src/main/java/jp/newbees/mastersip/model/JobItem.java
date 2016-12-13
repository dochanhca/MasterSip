package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class JobItem implements Serializable, Parcelable {
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

    public JobItem() {
    }

    protected JobItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<JobItem> CREATOR = new Parcelable.Creator<JobItem>() {
        @Override
        public JobItem createFromParcel(Parcel source) {
            return new JobItem(source);
        }

        @Override
        public JobItem[] newArray(int size) {
            return new JobItem[size];
        }
    };
}
