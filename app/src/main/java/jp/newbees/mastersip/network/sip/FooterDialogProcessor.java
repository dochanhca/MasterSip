package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.FooterDialogEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 5/16/17.
 */

public class FooterDialogProcessor extends BaseSocketProcessor<FooterDialogEvent> {
    @Override
    protected void didProcess(FooterDialogEvent data) {
        this.postEvent(data);
    }

    @Override
    protected FooterDialogEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = packetItem.getJsonData();
        return JSONUtils.genFooterDialogEvent(jData);
    }
}
