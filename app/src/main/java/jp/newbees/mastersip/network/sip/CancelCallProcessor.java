package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/9/17.
 */

public class CancelCallProcessor extends BaseSocketProcessor {

    @Override
    protected void didProcess(Object data) {
        //send event to UI
        this.postEvent(new ReceivingCallEvent(ReceivingCallEvent.RELEASE_CALL));
        //send event to service
        this.postEvent(new SendingCallEvent(SendingCallEvent.REJECT_CALL));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        return jData.getInt(Constant.JSON.CALL_ID) + "";
    }
}
