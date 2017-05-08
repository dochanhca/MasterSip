package jp.newbees.mastersip.network.sip;

import org.json.JSONException;

import jp.newbees.mastersip.event.CompetitorChangeBackgroundStateEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;

/**
 * Created by thangit14 on 4/27/17.
 */

public class CompetitorChangeBackgroundStateProcessor extends BaseSocketProcessor {
    @Override
    protected void didProcess(Object data) {
        String action = (String) data;
        this.postEvent(new CompetitorChangeBackgroundStateEvent(action));
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        return packetItem.getAction();
    }
}
