package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.JSONUtils;


/**
 * Created by vietbq on 1/6/17.
 */

public class ChattingProcessor extends BaseSocketProcessor<NewChatMessageEvent> {

    @Override
    protected void didProcess(NewChatMessageEvent data) {
        this.postEvent(data);
    }

    @Override
    protected NewChatMessageEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        UserItem me = ConfigManager.getInstance().getCurrentUser();
        JSONObject jData = new JSONObject(packetItem.getData());
        BaseChatItem baseChatItem = JSONUtils.parseChatItem(jData, me);
        return new NewChatMessageEvent(baseChatItem);
    }

}
