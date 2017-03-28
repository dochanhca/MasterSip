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
        String handleName = (String) data;
        this.postEvent(new BusyCallEvent(handleName));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        JSONObject jCaller = jData.getJSONObject(Constant.JSON.CALLER);
        return jCaller.getString("handle-name");
    }
}
