package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 3/13/17.
 */

public class RunOutOfCoinProcessor extends BaseSocketProcessor {

    @Override
    protected void didProcess(Object data) {
        this.postEvent(new RunOutOfCoinEvent());
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        Map<String, Integer> result = new HashMap<>();
        JSONObject jData = new JSONObject(packetItem.getData());

        int coin = jData.getInt(Constant.JSON.COINT);
        int total = 0;
        if (!jData.isNull(Constant.JSON.TOTAL)) {
            total = jData.getInt(Constant.JSON.TOTAL);
        }

        result.put(Constant.JSON.COINT, coin);
        result.put(Constant.JSON.TOTAL, total);
        return result;
    }
}
