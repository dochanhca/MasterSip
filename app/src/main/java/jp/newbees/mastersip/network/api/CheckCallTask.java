package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 1/6/17.
 */

public class CheckCallTask extends BaseTask {

    private final String callerExtension;
    private final String receiverExtension;
    private  int type;
    private  int kind;

    public CheckCallTask(Context context, UserItem caller, UserItem callee, int callType, int kind) {
        super(context);
        this.callerExtension = caller.getSipItem().getExtension();
        this.receiverExtension = callee.getSipItem().getExtension();
        this.type = callType;
        this.kind = kind;
    }


    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.CALLER, callerExtension);
        jParam.put(Constant.JSON.RECEIVER, receiverExtension);
        jParam.put(Constant.JSON.TYPE, type);
        jParam.put(Constant.JSON.K_KIND, kind);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHECK_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Map<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        Map<String, Object> result = new HashMap<>();

        int messageId = jData.getInt(Constant.JSON.K_MESSAGE_ID);

        result.put(Constant.JSON.K_MESSAGE_ID, messageId);

        if (!jData.isNull(Constant.JSON.K_MIN_POINT)) {
            int minPoint = jData.getInt(Constant.JSON.K_MIN_POINT);
            result.put(Constant.JSON.K_MIN_POINT, minPoint);
        }

        if (!jData.isNull(Constant.JSON.K_CALL_WAIT_ID)) {
            String callWaitId = jData.getString(Constant.JSON.K_CALL_WAIT_ID);
            result.put(Constant.JSON.K_CALL_WAIT_ID, callWaitId);
        }
        return result;
    }
}
