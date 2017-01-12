package jp.newbees.mastersip.event.chat;

import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by vietbq on 1/9/17.
 */

public class ChattingEvent {
    private BaseChatItem baseChatItem;

    public ChattingEvent(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }

    public BaseChatItem getBaseChatItem() {
        return baseChatItem;
    }
}
