package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/9/17.
 */

public class ChangeCallingStatusProcessor extends BaseSocketProcessor {

    @Override
    protected void didProcess(Object data) {
        int status = (int) ((Map<String, Object>) data).get(Constant.JSON.STATUS);

        if (status == Constant.SOCKET.STATUS_CALLING_CONNECTED) {
            this.postEvent(new ReceivingCallEvent(ReceivingCallEvent.OUTGOING_CONNECTED_CALL));
        }
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        Map<String, Object> result = new HashMap<>();
        result.put(Constant.JSON.CALL_ID, jData.getInt(Constant.JSON.CALL_ID) + "");
        result.put(Constant.JSON.STATUS, jData.getInt(Constant.JSON.STATUS));
        return result;
    }
}
