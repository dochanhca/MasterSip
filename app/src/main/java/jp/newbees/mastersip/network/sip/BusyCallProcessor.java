package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 3/9/17.
 */

public class BusyCallProcessor extends BaseSocketProcessor {

    @Override
    protected void didProcess(Object data) {
        String callId = (String) data;
        this.postEvent(new BusyCallEvent(callId));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        String callID = jData.getString(Constant.JSON.CALL_ID);
        return callID;
    }
}
