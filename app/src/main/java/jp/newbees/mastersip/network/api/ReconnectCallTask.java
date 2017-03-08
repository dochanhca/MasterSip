package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/8/17.
 */

public class ReconnectCallTask extends BaseTask<Void>{

    private String callId;

    public ReconnectCallTask(Context context, String callId) {
        super(context);
        this.callId = callId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.CALL_ID, callId);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.RECONNECT_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
