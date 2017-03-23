package jp.newbees.mastersip.model;

import android.os.Parcel;
import android.os.Parcelable;

import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/4/17.
 */

public class BaseChatItem implements Parcelable {

    private int roomId;
    private int messageId;
    private String fullDate;
    private String displayDate;
    private int chatType;
    private int cellIndex;
    private String cellIdentifier;
    private int messageState;
    private int roomType;
    private String shortDate;
    private String shortTimeStamp;
    /**
     * use for recycle view with header sticky
     */
    private int sectionFirstPosition;
    private int sectionManager;

    private UserItem sender;
    private UserItem sendee;

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

    /**
     * Default constructor
     */
    public BaseChatItem() {
    }

    /**
     * @param roomType
     * @param sender
     * @param sendee
     */
    public BaseChatItem(int roomType, UserItem sender, UserItem sendee) {
        this.roomType = roomType;
        this.sender = sender;
        this.sendee = sendee;
    }

    protected BaseChatItem(Parcel in) {
        roomId = in.readInt();
        messageId = in.readInt();
        fullDate = in.readString();
        displayDate = in.readString();
        chatType = in.readInt();
        cellIndex = in.readInt();
        cellIdentifier = in.readString();
        messageState = in.readInt();
        roomType = in.readInt();
        shortDate = in.readString();
        shortTimeStamp = in.readString();
        sectionFirstPosition = in.readInt();
        sectionManager = in.readInt();
        sender = in.readParcelable(UserItem.class.getClassLoader());
        sendee = in.readParcelable(UserItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(roomId);
        parcel.writeInt(messageId);
        parcel.writeString(fullDate);
        parcel.writeString(displayDate);
        parcel.writeInt(chatType);
        parcel.writeInt(cellIndex);
        parcel.writeString(cellIdentifier);
        parcel.writeInt(messageState);
        parcel.writeInt(roomType);
        parcel.writeString(shortDate);
        parcel.writeString(shortTimeStamp);
        parcel.writeInt(sectionFirstPosition);
        parcel.writeInt(sectionManager);
        parcel.writeParcelable(sender, i);
        parcel.writeParcelable(sendee, i);
    }

    public static final class ChatType implements Parcelable {
        public static final int CHAT_DELETED = 0;
        public static final int CHAT_VOICE = 1; //!!!
        public static final int CHAT_TEXT = 2;
        public static final int CHAT_VIDEO = 3;
        public static final int CHAT_IMAGE = 4;
        public static final int CHAT_VOICE_CALL = 5;
        public static final int CHAT_VIDEO_CALL = 6;
        public static final int CHAT_VIDEO_CHAT_CALL = 7;
        public static final int CHAT_GIFT = 9;
        public static final int HEADER = 11;

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

        protected ChatType(Parcel in) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final class CallType implements Parcelable {
        public static final int MISS_CALL = 3;
        public static final int CANCEL_CALL = 4;
        public static final int BUSY_CALL = 5;
        public static final int ANSWER_CALL = 6;
        public static final int END_CALL = 7;

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

        protected CallType(Parcel in) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final class RoomType implements Parcelable {
        public static final int ROOM_CHAT_CHAT = 1;
        public static final int ROOM_VIDEO_CHAT = 2;

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

        protected RoomType(Parcel in) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }

    public static final class MessageState implements Parcelable {
        public static final int STT_NONE = 0;
        public static final int STT_SENT = 1;
        public static final int STT_READ = 2;
        public static final int STT_DELIVERY = 3;
        public static final int STT_ERROR = 4;

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

        protected MessageState(Parcel in) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }
    }


    public UserItem getOwner() {
        return sender;
    }

    public void setOwner(UserItem owner) {
        this.sender = owner;
    }

    public UserItem getSendee() {
        return sendee;
    }

    public void setSendee(UserItem sendee) {
        this.sendee = sendee;
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

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
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

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
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


    public int getSectionManager() {
        return sectionManager;
    }

    public void setSectionManager(int sectionManager) {
        this.sectionManager = sectionManager;
    }

    public int getSectionFirstPosition() {
        return sectionFirstPosition;
    }

    public void setSectionFirstPosition(int sectionFirstPosition) {
        this.sectionFirstPosition = sectionFirstPosition;
    }

    public boolean isOwner() {
        String currentUser = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        return this.getOwner().getSipItem().getExtension().
                equalsIgnoreCase(currentUser);
    }
}
