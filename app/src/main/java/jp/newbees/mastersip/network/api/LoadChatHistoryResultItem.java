package jp.newbees.mastersip.network.api;

import java.util.ArrayList;
import java.util.HashMap;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 2/8/17.
 */
public class LoadChatHistoryResultItem {
    private HashMap<String, UserItem> members;
    private ArrayList<BaseChatItem> baseChatItems;

    public LoadChatHistoryResultItem(HashMap<String, UserItem> members, ArrayList<BaseChatItem> baseChatItems) {
        this.members = members;
        this.baseChatItems = baseChatItems;
    }

    public HashMap<String, UserItem> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, UserItem> members) {
        this.members = members;
    }

    public ArrayList<BaseChatItem> getBaseChatItems() {
        return baseChatItems;
    }

    public void setBaseChatItems(ArrayList<BaseChatItem> baseChatItems) {
        this.baseChatItems = baseChatItems;
    }
}
