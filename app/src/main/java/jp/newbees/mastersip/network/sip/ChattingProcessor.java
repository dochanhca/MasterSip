package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.EventManage;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.JSONUtils;


/**
 * Created by vietbq on 1/6/17.
 */

public class ChattingProcessor extends BaseSocketProcessor<BaseChatItem> {

    private static final String FROM_EXTENSION = "FROM EXTENSION";
    private static final String CHAT_MESSAGE = "CHAT MESSAGE";

    @Override
    protected void didProcess(BaseChatItem data) {
        String fromExtension = data.getSender().getSipItem().getExtension();
        int roomType = data.getRoomType();
        String eventName = EventManage.getInstance().genChattingEventName(fromExtension,roomType);
        this.postEventByName(data,eventName);
    }

    @Override
    protected BaseChatItem doInBackgroundData(PacketItem packetItem) throws JSONException {
        UserItem sender = ConfigManager.getInstance().getCurrentUser();
        JSONObject jData = new JSONObject(packetItem.getData());
        BaseChatItem baseChatItem = JSONUtils.parseChatItem(jData,sender);
        return baseChatItem;
    }

}
