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
    /**
     *
     * @param data
     */
    @Override
    protected void didProcess(Object data) {
        int coin = ((HashMap<String, Integer>) data).get(Constant.JSON.COINT);
        int total = ((HashMap<String, Integer>) data).get(Constant.JSON.TOTAL);

        /**
         * Only Coin changed event after call ended contain total field
         */
        if (total >= 0) {
            this.postEvent(new CoinChangedEvent(coin, total));
        }
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        Map<String, Integer> result = new HashMap<>();
        JSONObject jData = new JSONObject(packetItem.getData());

        int coin = jData.getInt(Constant.JSON.COINT);
        int total = -1;
        if (!jData.isNull(Constant.JSON.TOTAL)) {
            total = jData.getInt(Constant.JSON.TOTAL);
        }

        result.put(Constant.JSON.COINT, coin);
        result.put(Constant.JSON.TOTAL, total);
        return result;
    }
}
