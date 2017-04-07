package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vietbq on 1/6/17.
 */

public class PacketItem implements Parcelable {
    private String action;
    private String message;
    private String response;

    /**
     * Default constructor
     */
    public PacketItem() {
    }

    /**
     * @param action
     * @param message
     * @param response
     */
    public PacketItem(String action, String message, String response) {
        this.action = action;
        this.message = message;
        this.response = response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeString(this.message);
        dest.writeString(this.response);
    }

    protected PacketItem(Parcel in) {
        this.action = in.readString();
        this.message = in.readString();
        this.response = in.readString();
    }

    public static final Parcelable.Creator<PacketItem> CREATOR = new Parcelable.Creator<PacketItem>() {
        @Override
        public PacketItem createFromParcel(Parcel source) {
            return new PacketItem(source);
        }

        @Override
        public PacketItem[] newArray(int size) {
            return new PacketItem[size];
        }
    };

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return response;
    }

    public void setData(String data) {
        this.response = data;
    }

    public JSONObject getJsonData() throws JSONException {
        return new JSONObject(this.response);
    }
}
