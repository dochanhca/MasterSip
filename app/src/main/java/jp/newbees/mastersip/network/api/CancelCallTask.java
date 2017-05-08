package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/12/17.
 */

public final class CancelCallTask extends BaseTask<Void> {

    public static final int AUTO_HANGUP = 1;
    private final String callID;
    private int autoHangup;

    /**
     * This API uses for cancel a call
     * @param context
     * @param callID
     */
    public CancelCallTask(Context context, String callID) {
        this(context, callID , 0);
    }

    /**
     * This API uses for cancel a call
     * @param context
     * @param callID
     */
    public CancelCallTask(Context context, String callID, int autoHangup) {
        super(context);
        this.callID = callID;
        this.autoHangup = autoHangup;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.CALL_ID, callID);
        jParams.put(Constant.JSON.AUTO_HANGUP , autoHangup);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CANCEL_CALL_URL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        Logger.e("CancelCallTask", "cancel call Successfully");
        return null;
    }
}
