package jp.newbees.mastersip.presenter.top;

import android.content.Context;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.event.RoomChatEvent;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.DeleteChatRoomTask;
import jp.newbees.mastersip.network.api.GetListRoomTask;
import jp.newbees.mastersip.network.api.MarkAllMessageAsReadTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 1/4/17.
 */

public class ChatGroupPresenter extends BasePresenter {

    private int lastRoomId;
    private ChatGroupView chatGroupView;

    /**
     * Constructor ChatGroup
     *
     * @param context
     */
    public ChatGroupPresenter(Context context, ChatGroupView chatGroupView) {
        super(context);
        this.chatGroupView = chatGroupView;
    }

    public void loadChatRooms(int lastRoomId) {
        this.lastRoomId = lastRoomId;
        UserItem userItem = getCurrentUserItem();
        GetListRoomTask getListRoomTask = new GetListRoomTask(context, userItem, lastRoomId);
        requestToServer(getListRoomTask);
    }

    public void markAllMessageAsRead() {
        MarkAllMessageAsReadTask markAllMessageAsReadTask = new MarkAllMessageAsReadTask(context);
        requestToServer(markAllMessageAsReadTask);
    }

    public void deleteChatRoom(@Nullable List<Integer> chatRoomIds) {
        DeleteChatRoomTask deleteChatRoomTask;
        if (chatRoomIds == null) {
            deleteChatRoomTask = new DeleteChatRoomTask(context);
        } else {
            deleteChatRoomTask = new DeleteChatRoomTask(context, chatRoomIds);
        }
        requestToServer(deleteChatRoomTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetListRoomTask) {
            Map<String, Object> result = ((GetListRoomTask) task).getDataResponse();
            List<RoomChatItem> roomChatItems = (List<RoomChatItem>) result.get(GetListRoomTask.LIST_ROOM_CHAT);
            int numberOfRoomUnRead = (int) result.get(GetListRoomTask.NUMBER_OF_ROOM_UNREAD);
            this.handleRoomUnRead(numberOfRoomUnRead);

            if (lastRoomId != 0) {
                chatGroupView.didLoadMoreChatRoom(roomChatItems);
            } else {
                chatGroupView.didLoadChatRoom(roomChatItems);
            }
        } else if (task instanceof DeleteChatRoomTask) {
            chatGroupView.didDeleteChatRoom();
        } else if (task instanceof MarkAllMessageAsReadTask) {
            chatGroupView.didMarkAllMessageAsRead();
        }
    }

    /**
     * Fire event after archived number of room unread
     *
     * @param numberOfRoomUnRead
     */
    private void handleRoomUnRead(int numberOfRoomUnRead) {
        EventBus.getDefault().post(new RoomChatEvent(numberOfRoomUnRead));
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetListRoomTask) {
            chatGroupView.didLoadChatRoomError(errorCode, errorMessage);
        } else if (task instanceof MarkAllMessageAsReadTask) {
            chatGroupView.didMarkAllMessageAsReadError(errorCode, errorMessage);
        } else if (task instanceof DeleteChatRoomTask) {
            chatGroupView.didDeleteChatRoomError(errorCode, errorMessage);
        }
    }

    public interface ChatGroupView {
        void didLoadChatRoom(List<RoomChatItem> roomChatItems);

        void didLoadChatRoomError(int errorCode, String errorMessage);

        void didLoadMoreChatRoom(List<RoomChatItem> roomChatItems);

        void didDeleteChatRoom();

        void didDeleteChatRoomError(int errorCode, String errorMessage);

        void didMarkAllMessageAsRead();

        void didMarkAllMessageAsReadError(int errorCode, String errorMessage);

    }
}
