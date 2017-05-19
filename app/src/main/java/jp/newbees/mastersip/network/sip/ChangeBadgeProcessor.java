package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.ChangeBadgeEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vynv on 5/18/17.
 */

public class ChangeBadgeProcessor extends BaseSocketProcessor<ChangeBadgeEvent> {

    @Override
    protected void didProcess(ChangeBadgeEvent data) {
        this.postEvent(data);
    }

    @Override
    protected ChangeBadgeEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = packetItem.getJsonData();
        return JSONUtils.genChangeBadgeEvent(jData);
    }
}
