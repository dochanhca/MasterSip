package jp.newbees.mastersip.presenter.chatting;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.network.api.UpdateStateMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 4/10/17.
 * use for basic chat text: send and read chat text
 */

public class BasicChatPresenter extends BasePresenter{
    private SendChatTextListener sendChatTextListener;
    private ReadChatTextListener readChatTextListener;

    public BasicChatPresenter(Context context, SendChatTextListener sendChatTextListener, ReadChatTextListener readChatTextListener) {
        super(context);
        this.sendChatTextListener = sendChatTextListener;
        this.readChatTextListener = readChatTextListener;
    }

    public BasicChatPresenter(Context context, SendChatTextListener sendChatTextListener) {
        super(context);
        this.sendChatTextListener = sendChatTextListener;
    }

    public BasicChatPresenter(Context context, ReadChatTextListener readChatTextListener) {
        super(context);
        this.readChatTextListener = readChatTextListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            sendChatTextListener.didSendChatToServer(result);
        } else if (task instanceof UpdateStateMessageTask) {
            BaseChatItem result = ((UpdateStateMessageTask) task).getDataResponse();
            readChatTextListener.didSendingReadMessageToServer(result);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            sendChatTextListener.didChatError(errorCode, errorMessage);
        } else if (task instanceof UpdateStateMessageTask) {
            readChatTextListener.didSendingReadMessageToServerError(errorCode, errorMessage);
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
        readChatTextListener.onChatMessageEvent(newChatMessageEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateMessageChange(final ReceivingReadMessageEvent receivingReadMessageEvent) {
        sendChatTextListener.onStateMessageChange(receivingReadMessageEvent);
    }

    public boolean isMessageOfCurrentUser(UserItem user, UserItem currentUser) {
        return currentUser.getSipItem().getExtension().equalsIgnoreCase(user.getSipItem().getExtension());
    }

    public final void sendText(String content, UserItem sendee) {
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT, sender, sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context, textChatItem);
        requestToServer(messageTask);
    }

    public final void sendingReadMessageToServer(BaseChatItem baseChatItem) {
        UpdateStateMessageTask updateStateMessageTask = new UpdateStateMessageTask(context, baseChatItem);
        requestToServer(updateStateMessageTask);
    }

    public void sendingReadMessageUsingLinPhone(BaseChatItem baseChatItem, UserItem sender) {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        LinphoneHandler.getInstance().sendReadMessageEvent(currentUser.getSipItem().getExtension(),
                sender.getSipItem().getExtension(), baseChatItem);
    }
}
