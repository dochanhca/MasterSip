package jp.newbees.mastersip.network;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.eventbus.StateMessageChangeEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 1/12/17.
 */

public class UpdateMessageStateProcesser extends BaseSocketProcessor<StateMessageChangeEvent> {

    @Override
    protected void didProcess(StateMessageChangeEvent data) {
        this.postEvent(data);
    }

    @Override
    protected StateMessageChangeEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        BaseChatItem baseChatItem = JSONUtils.parseDateOnUpdateMessageState(jData);
        return new StateMessageChangeEvent(baseChatItem);
    }
}
