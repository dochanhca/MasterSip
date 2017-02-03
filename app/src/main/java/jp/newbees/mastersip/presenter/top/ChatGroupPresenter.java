package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.event.RoomChatEvent;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListRoomTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/4/17.
 */

public class ChatGroupPresenter extends BasePresenter {
    private static final String NO_MORE_DATA = "";
    private static final String FIRST_PAGE = "";
    private String nextPage;
    private boolean isLoadMore = false;

    private ChatGroupView chatGroupView;

    /**
     * Constructor ChatGroup
     *
     * @param context
     */
    public ChatGroupPresenter(Context context, ChatGroupView chatGroupView) {
        super(context);
        this.nextPage = null;
        this.chatGroupView = chatGroupView;
    }

    /**
     * Get list room
     */
    public void loadListRoom() {
        loadData(false, FIRST_PAGE);
    }

    private void loadData(boolean isLoadMore, String page) {
        this.isLoadMore = isLoadMore;
        UserItem userItem = getCurrentUserItem();
        GetListRoomTask getListRoomTask = new GetListRoomTask(getContext(), userItem, page);
        requestToServer(getListRoomTask);
    }

    public void loadMoreRoom() {
        if (hasMoreData()) {
            loadData(true, nextPage);
        }
    }

    public boolean hasMoreData() {
        return !nextPage.equalsIgnoreCase(NO_MORE_DATA);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetListRoomTask) {
            Map<String, Object> result = ((GetListRoomTask) task).getDataResponse();
            List<RoomChatItem> roomChatItems = (List<RoomChatItem>) result.get(GetListRoomTask.LIST_ROOM_CHAT);
            int numberOfRoomUnRead = (int) result.get(GetListRoomTask.NUMBER_OF_ROOM_UNREAD);
            this.nextPage = (String) result.get(GetListRoomTask.NEXT_PAGE);
            this.handleRoomUnRead(numberOfRoomUnRead);

            if (isLoadMore) {
                chatGroupView.didLoadMoreChatRoom(roomChatItems);
            } else {
                chatGroupView.didLoadChatRoom(roomChatItems);
            }
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
        Logger.e(TAG, errorMessage);
        chatGroupView.didLoadChatRoomFailure(errorCode, errorMessage);
    }

    public interface ChatGroupView {
        void didLoadChatRoom(List<RoomChatItem> roomChatItems);

        void didLoadChatRoomFailure(int errorCode, String errorMessage);

        void didLoadMoreChatRoom(List<RoomChatItem> roomChatItems);

    }
}
