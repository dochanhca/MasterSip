package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 3/16/17.
 */

public class PaymentAdOnItem implements Parcelable {

    private String id;
    private int point;
    private int cash;
    private int status;

    public PaymentAdOnItem() {

    }

    protected PaymentAdOnItem(Parcel in) {
        id = in.readString();
        point = in.readInt();
        cash = in.readInt();
        status = in.readInt();
    }

    public static final Creator<PaymentAdOnItem> CREATOR = new Creator<PaymentAdOnItem>() {
        @Override
        public PaymentAdOnItem createFromParcel(Parcel in) {
            return new PaymentAdOnItem(in);
        }

        @Override
        public PaymentAdOnItem[] newArray(int size) {
            return new PaymentAdOnItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(point);
        dest.writeInt(cash);
        dest.writeInt(status);
    }
}
