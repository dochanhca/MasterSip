package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ducpv on 2/9/17.
 */

public class ChattingGalleryItem extends GalleryItem implements Parcelable {

    private int roomId;
    private int minPoint;
    private UserItem sender;

    public ChattingGalleryItem() {

    }

    protected ChattingGalleryItem(Parcel in) {
        super(in);
        roomId = in.readInt();
        minPoint = in.readInt();
        sender = in.readParcelable(UserItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(roomId);
        dest.writeInt(minPoint);
        dest.writeParcelable(sender, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChattingGalleryItem> CREATOR = new Creator<ChattingGalleryItem>() {
        @Override
        public ChattingGalleryItem createFromParcel(Parcel in) {
            return new ChattingGalleryItem(in);
        }

        @Override
        public ChattingGalleryItem[] newArray(int size) {
            return new ChattingGalleryItem[size];
        }
    };

    public UserItem getSender() {
        return sender;
    }

    public void setSender(UserItem sender) {
        this.sender = sender;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(int minPoint) {
        this.minPoint = minPoint;
    }
}
