package jp.newbees.mastersip.event;

/**
 * Created by vietbq on 2/2/17.
 */

public class RoomChatEvent {
    private int numberOfRoomUnRead;

    /**
     * Number of room unread
     * @param numberOfRoomUnRead
     */
    public RoomChatEvent(int numberOfRoomUnRead) {
        this.numberOfRoomUnRead = numberOfRoomUnRead;
    }

    public int getNumberOfRoomUnRead() {
        return numberOfRoomUnRead;
    }
}
