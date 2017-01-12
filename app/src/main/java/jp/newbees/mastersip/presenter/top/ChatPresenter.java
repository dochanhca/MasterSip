package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/11/17.
 */

public class ChatPresenter extends BasePresenter {

    private ChatPresenterListener listener;

    public ChatPresenter(Context context, ChatPresenterListener listener) {
        super(context);
        this.listener = listener;
    }

    public interface ChatPresenterListener{
        void didSendChatToServer(BaseChatItem baseChatItem);

        void didChatError(int errorCode, String errorMessage);
    }

    public final void sendText(String content,UserItem sendee){
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT,sender,sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context,textChatItem);
        requestToServer(messageTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask) {
            BaseChatItem result = ((SendTextMessageTask) task).getDataResponse();
            listener.didSendChatToServer(result);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        listener.didChatError(errorCode, errorMessage);
    }
}
