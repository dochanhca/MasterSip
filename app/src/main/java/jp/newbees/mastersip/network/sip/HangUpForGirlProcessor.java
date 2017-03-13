package jp.newbees.mastersip.network.sip;

import org.json.JSONException;

import jp.newbees.mastersip.event.call.HangUpForGirlEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;

/**
 * Created by ducpv on 3/10/17.
 */

public class HangUpForGirlProcessor extends BaseSocketProcessor {
    @Override
    protected void didProcess(Object data) {
        this.postEvent(new HangUpForGirlEvent());
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        return null;
    }
}
