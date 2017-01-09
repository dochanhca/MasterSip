package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vietbq on 1/4/17.
 */

public class BaseChatItem implements Parcelable, Serializable {

    public BaseChatItem(int roomType, UserItem sender, UserItem sendee) {
        this.roomType = roomType;
        this.sender = sender;
        this.sendee = sendee;
    }

    public static final class ChatType implements Parcelable{
        public final static int CHAT_DELETED = 0;
        public final static int CHAT_VOICE = 1;
        public final static int CHAT_TEXT = 2;
        public final static int CHAT_VIDEO = 3;
        public final static int CHAT_IMAGE = 4;
        public final static int CHAT_VOICE_CALL = 5;
        public final static int CHAT_VIDEO_CALL = 6;
        public final static int CHAT_VIDEO_CHAT_CALL = 7;
        public final static int CHAT_GIFT = 9;

        public final static int CHAT_TEXT_REPLY = 10;


        protected ChatType(Parcel in) {
        }

        public static final Creator<ChatType> CREATOR = new Creator<ChatType>() {
            @Override
            public ChatType createFromParcel(Parcel in) {
                return new ChatType(in);
            }

            @Override
            public ChatType[] newArray(int size) {
                return new ChatType[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final class CallType implements Parcelable{
        public final static int MISS_CALL = 3;
        public final static int CANCEL_CALL = 4;
        public final static int BUSY_CALL = 5;
        public final static int ANSWER_CALL = 6;
        public final static int END_CALL = 7;

        protected CallType(Parcel in) {
        }

        public static final Creator<CallType> CREATOR = new Creator<CallType>() {
            @Override
            public CallType createFromParcel(Parcel in) {
                return new CallType(in);
            }

            @Override
            public CallType[] newArray(int size) {
                return new CallType[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final class RoomType implements Parcelable{
        public final static int ROOM_CHAT_CHAT = 1;
        public final static int ROOM_VIDEO_CHAT = 2;

        protected RoomType(Parcel in) {
        }

        public static final Creator<RoomType> CREATOR = new Creator<RoomType>() {
            @Override
            public RoomType createFromParcel(Parcel in) {
                return new RoomType(in);
            }

            @Override
            public RoomType[] newArray(int size) {
                return new RoomType[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }


    public static final class MessageState implements Parcelable{
        public final static int STT_NONE = 0;
        public final static int STT_SENT = 1;
        public final static int STT_READ = 2;
        public final static int STT_DELIVERY = 3;
        public final static int STT_ERROR = 4;

        protected MessageState(Parcel in) {
        }

        public static final Creator<MessageState> CREATOR = new Creator<MessageState>() {
            @Override
            public MessageState createFromParcel(Parcel in) {
                return new MessageState(in);
            }

            @Override
            public MessageState[] newArray(int size) {
                return new MessageState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final Creator<BaseChatItem> CREATOR = new Creator<BaseChatItem>() {
        @Override
        public BaseChatItem createFromParcel(Parcel in) {
            return new BaseChatItem(in);
        }

        @Override
        public BaseChatItem[] newArray(int size) {
            return new BaseChatItem[size];
        }
    };

    public UserItem getSender() {
        return sender;
    }

    public void setSender(UserItem sender) {
        this.sender = sender;
    }

    //    @property (nonatomic) NSString *imageNameDefault;
    private UserItem sender;
    private UserItem sendee;

    public UserItem getSendee() {
        return sendee;
    }

    public void setSendee(UserItem sendee) {
        this.sendee = sendee;
    }

    private boolean isSender; //isHost
    private int roomId;
    private int messageId;
    private String fullDate;

    private ChatType chatType;

    private int cellIndex;
    private String cellIdentifier; //For cell;
//    @property (nonatomic) BOOL isSendFalse;
    private MessageState messageState;
    private int roomType;
    private String shortDate;
    private String shortTimeStamp;


    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public String getCellIdentifier() {
        return cellIdentifier;
    }

    public void setCellIdentifier(String cellIdentifier) {
        this.cellIdentifier = cellIdentifier;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public String getShortDate() {
        return shortDate;
    }

    public void setShortDate(String shortDate) {
        this.shortDate = shortDate;
    }

    public String getShortTimeStamp() {
        return shortTimeStamp;
    }

    public void setShortTimeStamp(String shortTimeStamp) {
        this.shortTimeStamp = shortTimeStamp;
    }

    public BaseChatItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.sender, flags);
        dest.writeParcelable(this.sendee, flags);
        dest.writeByte(this.isSender ? (byte) 1 : (byte) 0);
        dest.writeInt(this.roomId);
        dest.writeInt(this.messageId);
        dest.writeString(this.fullDate);
        dest.writeParcelable(this.chatType, flags);
        dest.writeInt(this.cellIndex);
        dest.writeString(this.cellIdentifier);
        dest.writeParcelable(this.messageState, flags);
        dest.writeInt(this.roomType);
        dest.writeString(this.shortDate);
        dest.writeString(this.shortTimeStamp);
    }

    protected BaseChatItem(Parcel in) {
        this.sender = in.readParcelable(UserItem.class.getClassLoader());
        this.sendee = in.readParcelable(UserItem.class.getClassLoader());
        this.isSender = in.readByte() != 0;
        this.roomId = in.readInt();
        this.messageId = in.readInt();
        this.fullDate = in.readString();
        this.chatType = in.readParcelable(ChatType.class.getClassLoader());
        this.cellIndex = in.readInt();
        this.cellIdentifier = in.readString();
        this.messageState = in.readParcelable(MessageState.class.getClassLoader());
        this.roomType = in.readInt();
        this.shortDate = in.readString();
        this.shortTimeStamp = in.readString();
    }

}
