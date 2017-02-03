package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 2/3/17.
 */

public class GiftChatItem extends BaseChatItem implements Parcelable {
    public void setGiftItem(GiftItem giftItem) {
        this.giftItem = giftItem;
    }

    public GiftItem getGiftItem() {
        return giftItem;
    }

    private GiftItem giftItem;

    public String getContent() {
        return content;
    }

    private String content;

    public GiftChatItem() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.giftItem, flags);
        dest.writeString(this.content);
    }

    protected GiftChatItem(Parcel in) {
        super(in);
        this.giftItem = in.readParcelable(GiftItem.class.getClassLoader());
        this.content = in.readString();
    }

    public static final Creator<GiftChatItem> CREATOR = new Creator<GiftChatItem>() {
        @Override
        public GiftChatItem createFromParcel(Parcel source) {
            return new GiftChatItem(source);
        }

        @Override
        public GiftChatItem[] newArray(int size) {
            return new GiftChatItem[size];
        }
    };
}
