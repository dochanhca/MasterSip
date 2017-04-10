package jp.newbees.mastersip.network.sip;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.event.GSMCallEvent;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.sip.base.BaseSocketProcessor;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 4/5/17.
 */

public class PauseCallProcessor extends BaseSocketProcessor<GSMCallEvent> {

    @Override
    protected void didProcess(GSMCallEvent data) {
        super.postEvent(data);
    }

    @Override
    protected GSMCallEvent doInBackgroundData(PacketItem packetItem) throws JSONException {
        JSONObject jData = packetItem.getJsonData();
        String userId = jData.getString(Constant.JSON.EXTENSION);
        int gsmState = jData.getInt(Constant.JSON.GSM_STATE);
        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        GSMCallEvent event = new GSMCallEvent(userItem, gsmState);
        return event;
    }
}
