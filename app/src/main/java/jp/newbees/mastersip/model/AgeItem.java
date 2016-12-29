package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 12/28/16.
 */

public class AgeItem implements Parcelable, Cloneable {

    private SelectionItem selectionItem;
    private boolean isDisable;

    public AgeItem() {
    }

    public AgeItem(SelectionItem selectionItem, boolean isDisable) {
        this.selectionItem = selectionItem;
        this.isDisable = isDisable;
    }

    protected AgeItem(Parcel in) {
        selectionItem = in.readParcelable(SelectionItem.class.getClassLoader());
        isDisable = in.readByte() != 0;
    }

    public static final Creator<AgeItem> CREATOR = new Creator<AgeItem>() {
        @Override
        public AgeItem createFromParcel(Parcel in) {
            return new AgeItem(in);
        }

        @Override
        public AgeItem[] newArray(int size) {
            return new AgeItem[size];
        }
    };

    public SelectionItem getSelectionItem() {
        return selectionItem;
    }

    public void setSelectionItem(SelectionItem selectionItem) {
        this.selectionItem = selectionItem;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(selectionItem, i);
        parcel.writeByte((byte) (isDisable ? 1 : 0));
    }

    @Override
    public AgeItem clone() {
        AgeItem clone = null;
        try {
            clone = (AgeItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // won't happen
        }
        return clone;
    }
}
