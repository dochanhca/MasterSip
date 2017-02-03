package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 12/6/16.
 */

public class SelectionItem implements Serializable, Parcelable {

    /**
     * @param itemId
     * @param itemTitle
     */
    public SelectionItem(int itemId, String itemTitle) {
        this.id = itemId;
        this.title = itemTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public SelectionItem() {
        this.id = 0;
        this.title = "";
    }

    protected SelectionItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<SelectionItem> CREATOR = new Parcelable.Creator<SelectionItem>() {
        @Override
        public SelectionItem createFromParcel(Parcel source) {
            return new SelectionItem(source);
        }

        @Override
        public SelectionItem[] newArray(int size) {
            return new SelectionItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectionItem that = (SelectionItem) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
