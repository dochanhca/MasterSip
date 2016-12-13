package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class TypePeopleItem implements Parcelable, Serializable{
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

    public TypePeopleItem() {
    }

    protected TypePeopleItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<TypePeopleItem> CREATOR = new Parcelable.Creator<TypePeopleItem>() {
        @Override
        public TypePeopleItem createFromParcel(Parcel source) {
            return new TypePeopleItem(source);
        }

        @Override
        public TypePeopleItem[] newArray(int size) {
            return new TypePeopleItem[size];
        }
    };
}
