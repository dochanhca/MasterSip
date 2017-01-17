package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import jp.newbees.mastersip.eventbus.SendingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.network.api.UpdateStateMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/11/17.
 */

public class ChatPresenter extends BasePresenter {

    private ChatPresenterListener chatPresenterListener;
    private SendingReadMessageToServerListener sendingReadMessageToServerListener;

    public ChatPresenter(Context context, ChatPresenterListener chatPresenterListener,
                         SendingReadMessageToServerListener sendingReadMessageToServerListener) {
        super(context);
        this.chatPresenterListener = chatPresenterListener;
        this.sendingReadMessageToServerListener = sendingReadMessageToServerListener;
    }

    public interface ChatPresenterListener{
        void didSendChatToServer(BaseChatItem baseChatItem);

        void didChatError(int errorCode, String errorMessage);

    }

    public interface SendingReadMessageToServerListener {
        void didSendingReadMessageToServer(BaseChatItem baseChatItem);

        void didSendingReadMessageToServerError(int errorCode, String errorMessage);
    }

    public final void sendText(String content,UserItem sendee){
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT,sender,sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context,textChatItem);
        requestToServer(messageTask);
    }

    public final void sendingReadMessageToServer(BaseChatItem baseChatItem) {
        UpdateStateMessageTask updateStateMessageTask = new UpdateStateMessageTask(context, baseChatItem);
        requestToServer(updateStateMessageTask);
    }

    public void sendingReadMessageUsingLinPhone(BaseChatItem baseChatItem, UserItem sender) {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        EventBus.getDefault().post(new SendingReadMessageEvent(baseChatItem, currentUser, sender));
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            chatPresenterListener.didSendChatToServer(result);
        } else if (task instanceof UpdateStateMessageTask) {
            BaseChatItem result = ((UpdateStateMessageTask) task).getDataResponse();
            sendingReadMessageToServerListener.didSendingReadMessageToServer(result);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            chatPresenterListener.didChatError(errorCode, errorMessage);
        } else if (task instanceof UpdateStateMessageTask) {
            sendingReadMessageToServerListener.didSendingReadMessageToServerError(errorCode, errorMessage);
        }
    }
}
