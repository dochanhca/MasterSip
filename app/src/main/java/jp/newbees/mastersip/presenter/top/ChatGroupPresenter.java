package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendTextMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/4/17.
 */

public class ChatGroupPresenter extends BasePresenter {

    public ChatGroupPresenter(Context context) {
        super(context);
    }

    public final void sendText(String content,UserItem sendee){
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        TextChatItem textChatItem = new TextChatItem(content, BaseChatItem.RoomType.ROOM_CHAT_CHAT,sender,sendee);
        SendTextMessageTask messageTask = new SendTextMessageTask(context,textChatItem);
        requestToServer(messageTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendTextMessageTask){

        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        Logger.e(TAG,errorMessage);
    }

}
