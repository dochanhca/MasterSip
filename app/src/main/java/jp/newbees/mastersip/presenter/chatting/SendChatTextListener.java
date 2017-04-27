package jp.newbees.mastersip.presenter.chatting;

import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 4/10/17.
 */

public interface SendChatTextListener {
    void didSendChatToServer(BaseChatItem baseChatItem);

    void didChatError(int errorCode, String errorMessage);
}
