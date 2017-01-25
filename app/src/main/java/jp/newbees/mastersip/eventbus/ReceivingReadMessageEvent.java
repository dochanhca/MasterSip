package jp.newbees.mastersip.eventbus;

import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 1/12/17.
 */
public class ReceivingReadMessageEvent {
    private BaseChatItem baseChatItem;

    /**
     *
     * @param baseChatItem
     */
    public ReceivingReadMessageEvent(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }

    public BaseChatItem getBaseChatItem() {
        return baseChatItem;
    }

    public void setBaseChatItem(BaseChatItem baseChatItem) {
        this.baseChatItem = baseChatItem;
    }
}
