package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 4/20/17.
 */

public class UnsubscribeOnlineNotifyTask extends BaseTask<Void> {

    private String destUserId;

    public UnsubscribeOnlineNotifyTask(Context context, String destUserId) {
        super(context);
        this.destUserId = destUserId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.DEST_USER_ID, destUserId);
        return jsonObject;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.UNSUBSCRIBE_ONLINE_NOTIFY;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
