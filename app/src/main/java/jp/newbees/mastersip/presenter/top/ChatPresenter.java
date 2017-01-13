package jp.newbees.mastersip.presenter.top;

import android.content.Context;

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
    private UpdateStateMessageToServerListener updateStateMessageToServerListener;

    public ChatPresenter(Context context, ChatPresenterListener chatPresenterListener,
                         UpdateStateMessageToServerListener updateStateMessageToServerListener) {
        super(context);
        this.chatPresenterListener = chatPresenterListener;
        this.updateStateMessageToServerListener = updateStateMessageToServerListener;
    }

    public interface ChatPresenterListener{
        void didSendChatToServer(BaseChatItem baseChatItem);

        void didChatError(int errorCode, String errorMessage);

    }

    public interface UpdateStateMessageToServerListener {
        void didUpdateStateMessageToServer();

        void didUpdateStateMessageToServerError(int errorCode, String errorMessage);
    }

    public final void sendText(String content,UserItem sendee){
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT,sender,sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context,textChatItem);
        requestToServer(messageTask);
    }

    public final void updateStateMessage(int messageID) {
        UpdateStateMessageTask updateStateMessageTask = new UpdateStateMessageTask(context, messageID);
        requestToServer(updateStateMessageTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            chatPresenterListener.didSendChatToServer(result);
        } else if (task instanceof UpdateStateMessageTask) {
            updateStateMessageToServerListener.didUpdateStateMessageToServer();
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendTextMessageTask) {
            chatPresenterListener.didChatError(errorCode, errorMessage);
        } else if (task instanceof UpdateStateMessageTask) {
            updateStateMessageToServerListener.didUpdateStateMessageToServerError(errorCode, errorMessage);
        }
    }
}
