package jp.newbees.mastersip.network.sip;

import org.json.JSONException;

import jp.newbees.mastersip.event.call.AdminHangUpEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;

/**
 * Created by ducpv on 3/13/17.
 */

public class AdminHangUpProcessor extends BaseSocketProcessor {
    @Override
    protected void didProcess(Object data) {
        this.postEvent(new AdminHangUpEvent());
    }

    @Override
    protected Object doInBackgroundData(PacketItem packetItem) throws JSONException {
        return null;
    }
}
