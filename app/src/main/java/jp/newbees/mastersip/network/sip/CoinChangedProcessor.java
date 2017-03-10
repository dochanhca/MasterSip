package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/12/17.
 */

public class CoinChangedProcessor extends BaseSocketProcessor {
    @Override
    protected void didProcess(Object data) {
        int coint = ((HashMap<String, Integer>) data).get(Constant.JSON.COINT);
        int total = ((HashMap<String, Integer>) data).get(Constant.JSON.TOTAL);
        this.postEvent(new CoinChangedEvent(coint, total));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        Map<String, Integer> result = new HashMap<>();
        JSONObject jData = new JSONObject(packetItem.getData());

        int coint = jData.getInt(Constant.JSON.COINT);
        int total = 0;
        if (!jData.isNull(Constant.JSON.TOTAL)) {
            total = jData.getInt(Constant.JSON.TOTAL);
        }

        result.put(Constant.JSON.COINT, coint);
        result.put(Constant.JSON.TOTAL, total);
        return result;
    }
}
