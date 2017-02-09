package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vietbq on 2/3/17.
 */

public class GiftChatItem extends BaseChatItem implements Parcelable {
    protected GiftChatItem(Parcel in) {
        giftItem = in.readParcelable(GiftItem.class.getClassLoader());
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(giftItem, flags);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GiftChatItem> CREATOR = new Creator<GiftChatItem>() {
        @Override
        public GiftChatItem createFromParcel(Parcel in) {
            return new GiftChatItem(in);
        }

        @Override
        public GiftChatItem[] newArray(int size) {
            return new GiftChatItem[size];
        }
    };

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
}
