package jp.newbees.mastersip.presenter.chatting;

import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 4/10/17.
 */

public interface ReadChatTextListener {
    void didSendingReadMessageToServer(BaseChatItem baseChatItem);

    void didSendingReadMessageToServerError(int errorCode, String errorMessage);

    void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent);
}
