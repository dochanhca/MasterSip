package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

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
        this.postEvent(new CoinChangedEvent((Integer) data));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = new JSONObject(packetItem.getData());
        int coin = jData.getInt(Constant.JSON.COINT);
        return coin;
    }
}
