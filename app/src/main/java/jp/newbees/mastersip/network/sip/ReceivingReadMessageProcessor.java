package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 1/12/17.
 */

public class ReceivingReadMessageProcessor extends BaseSocketProcessor<ReceivingReadMessageEvent> {

    @Override
    protected void didProcess(ReceivingReadMessageEvent data) {
        this.postEvent(data);
    }

    @Override
    protected ReceivingReadMessageEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        BaseChatItem baseChatItem = JSONUtils.parseDateOnUpdateMessageState(jData);
        return new ReceivingReadMessageEvent(baseChatItem);
    }
}
