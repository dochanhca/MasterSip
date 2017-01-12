package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/12/17.
 */

public final class CancelCallTask extends BaseTask<Void> {

    private final String waitingCallId;
    private final int callType;
    private final String caller;
    private final String callee;

    public CancelCallTask(Context context, String caller, String callee, int callType, String waitingCallId) {
        super(context);
        this.waitingCallId = waitingCallId;
        this.callType = callType;
        this.caller = caller;
        this.callee = callee;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.CALLER, caller);
        jParams.put(Constant.JSON.RECEIVER, callee);
        jParams.put(Constant.JSON.TYPE, callType);
        jParams.put(Constant.JSON.WAIT_CALL_ID, waitingCallId);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CANCEL_CALL_URL;
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
