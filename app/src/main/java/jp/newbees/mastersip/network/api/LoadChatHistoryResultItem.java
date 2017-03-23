package jp.newbees.mastersip.network.api;

import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 2/8/17.
 */
public class LoadChatHistoryResultItem {
    private Map<String, UserItem> members;
    private List<BaseChatItem> baseChatItems;

    public LoadChatHistoryResultItem(Map<String, UserItem> members, List<BaseChatItem> baseChatItems) {
        this.members = members;
        this.baseChatItems = baseChatItems;
    }

    public Map<String, UserItem> getMembers() {
        return members;
    }

    public void setMembers(Map<String, UserItem> members) {
        this.members = members;
    }

    public List<BaseChatItem> getBaseChatItems() {
        return baseChatItems;
    }

    public void setBaseChatItems(List<BaseChatItem> baseChatItems) {
        this.baseChatItems = baseChatItems;
    }
}
