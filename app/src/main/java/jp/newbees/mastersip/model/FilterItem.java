package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/20/16.
 */

public class FilterItem implements Parcelable, Serializable {

    public FilterItem() {
        this(-1,-1, new ArrayList<SelectionItem>(), false, new SelectionItem(Constant.Application.LAST_LOGIN_24HOUR,""), Constant.API.NEW_USER);
    }

    public FilterItem(int minAge, int maxAge, ArrayList<SelectionItem> locations, boolean isLogin24hours, SelectionItem orderBy, int filterType) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.locations = locations;
        this.isLogin24hours = isLogin24hours;
        this.orderBy = orderBy;
        this.filterType = filterType;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public ArrayList<SelectionItem> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<SelectionItem> locations) {
        this.locations = locations;
    }

    public boolean isLogin24hours() {
        return isLogin24hours;
    }

    public void setLogin24hours(boolean login24hours) {
        isLogin24hours = login24hours;
    }

    public SelectionItem getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SelectionItem orderBy) {
        this.orderBy = orderBy;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    private int minAge;
    private int maxAge;
    private ArrayList<SelectionItem> locations;
    private boolean isLogin24hours;
    private SelectionItem orderBy;
    private int filterType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.minAge);
        dest.writeInt(this.maxAge);
        dest.writeTypedList(this.locations);
        dest.writeByte(this.isLogin24hours ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.orderBy, flags);
        dest.writeInt(this.filterType);
    }



    protected FilterItem(Parcel in) {
        this.minAge = in.readInt();
        this.maxAge = in.readInt();
        this.locations = in.createTypedArrayList(SelectionItem.CREATOR);
        this.isLogin24hours = in.readByte() != 0;
        this.orderBy = in.readParcelable(SelectionItem.class.getClassLoader());
        this.filterType = in.readInt();
    }

    public static final Parcelable.Creator<FilterItem> CREATOR = new Parcelable.Creator<FilterItem>() {
        @Override
        public FilterItem createFromParcel(Parcel source) {
            return new FilterItem(source);
        }

        @Override
        public FilterItem[] newArray(int size) {
            return new FilterItem[size];
        }
    };
}
