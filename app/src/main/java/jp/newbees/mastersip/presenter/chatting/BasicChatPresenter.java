package jp.newbees.mastersip.presenter.chatting;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 4/10/17.
 * use for basic chat text: send and read chat text
 */

public class BasicChatPresenter extends BasePresenter{
    private SendChatTextListener sendChatTextListener;
    private ReceiveChatTextListener receiveChatTextListener;

    public BasicChatPresenter(Context context, SendChatTextListener sendChatTextListener, ReceiveChatTextListener receiveChatTextListener) {
        super(context);
        this.sendChatTextListener = sendChatTextListener;
        this.receiveChatTextListener = receiveChatTextListener;
    }

    public BasicChatPresenter(Context context, SendChatTextListener sendChatTextListener) {
        super(context);
        this.sendChatTextListener = sendChatTextListener;
    }

    public BasicChatPresenter(Context context, ReceiveChatTextListener receiveChatTextListener) {
        super(context);
        this.receiveChatTextListener = receiveChatTextListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            sendChatTextListener.didSendChatToServer(result);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            sendChatTextListener.didChatError(errorCode, errorMessage);
        }
    }

    public final void unregisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public final void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent) {
        receiveChatTextListener.onChatMessageEvent(newChatMessageEvent);
    }

    public boolean isMessageOfCurrentUser(UserItem user, UserItem currentUser) {
        return currentUser.getSipItem().getExtension().equalsIgnoreCase(user.getSipItem().getExtension());
    }

    private final void sendText(String content, UserItem sendee, int roomType) {
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, roomType, sender, sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context, textChatItem);
        requestToServer(messageTask);
    }

    public final void sendChatText(String content, UserItem sendee) {
        sendText(content, sendee, BaseChatItem.RoomType.ROOM_CHAT_CHAT);
    }

    public final void sendVideoChatText(String content, UserItem sendee) {
        sendText(content, sendee, BaseChatItem.RoomType.ROOM_VIDEO_CHAT);
    }
}
