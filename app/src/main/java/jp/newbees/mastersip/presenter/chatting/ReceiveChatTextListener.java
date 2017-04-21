package jp.newbees.mastersip.presenter.chatting;

import jp.newbees.mastersip.eventbus.NewChatMessageEvent;

/**
 * Created by thangit14 on 4/10/17.
 */

public interface ReceiveChatTextListener {
    void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent);
}
