package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 3/9/17.
 */

public class JoinCallTask extends BaseTask<Void> {

    private String callId;

    /**
     * This API uses for join call
     * @param context
     * @param callId
     */
    public JoinCallTask(Context context, String callId) {
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
        return Constant.API.JOIN_TO_CALL;
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
