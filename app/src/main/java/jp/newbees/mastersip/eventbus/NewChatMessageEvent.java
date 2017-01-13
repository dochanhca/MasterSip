package jp.newbees.mastersip.eventbus;

import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 1/12/17.
 */

public class NewChatMessageEvent {
    private BaseChatItem baseChatItem;

    public NewChatMessageEvent(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }

    public BaseChatItem getBaseChatItem() {
        return baseChatItem;
    }

    public void setBaseChatItem(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }
}
