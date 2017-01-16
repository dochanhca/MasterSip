package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/12/17.
 */

public class UpdateStateMessageTask extends BaseTask<BaseChatItem> {
    private BaseChatItem baseChatItem;
    private String senderExtension;

    public UpdateStateMessageTask(Context context, BaseChatItem baseChatItem) {
        super(context);
        this.senderExtension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        this.baseChatItem = baseChatItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.MESSAGE_ID, baseChatItem.getMessageId());
        jParams.put(Constant.JSON.EXTENSION, senderExtension);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.UPDATE_STATE_MESSAGE_URL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected BaseChatItem didResponse(JSONObject data) throws JSONException {
        return baseChatItem;
    }

}
