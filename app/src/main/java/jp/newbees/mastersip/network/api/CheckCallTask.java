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
    public final static String CALL_TYPE = "Call TYPE";
    public static final String CALLEE = "CALLEE";
    public static final String CALLEE_ONLINE = "CALLEE_ONLINE";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    public final static String WAITING_CALL_ID = "WAITING_CALL_ID";

    private final String callerExtension;
    private final UserItem callee;
    private int type;
    private int kind;

    public CheckCallTask(Context context, UserItem caller, UserItem callee, int callType, int callFrom) {
        super(context);
        this.callerExtension = caller.getSipItem().getExtension();
        this.callee = callee;
        this.type = callType;
        this.kind = callFrom;
    }


    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.CALLER, callerExtension);
        jParam.put(Constant.JSON.RECEIVER, callee.getSipItem().getExtension());
        jParam.put(Constant.JSON.TYPE, type);
        jParam.put(Constant.JSON.KIND, kind);
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
        result.put(CALL_TYPE, type);
        result.put(CALLEE, callee);

        if (!jData.getString(Constant.JSON.MESSAGE_ID).isEmpty()) {
            int messageId = Integer.parseInt(jData.getString(Constant.JSON.MESSAGE_ID));
            result.put(MESSAGE_ID, messageId);
        }

        if (jData.has(Constant.JSON.RECEIVER_STATUS)) {
            result.put(CheckCallTask.CALLEE_ONLINE, false);
        } else {
            result.put(CheckCallTask.CALLEE_ONLINE, true);
        }

        if (jData.has(Constant.JSON.MIN_POINT)) {
            int minPoint = jData.getInt(Constant.JSON.MIN_POINT);
            result.put(Constant.JSON.MIN_POINT, minPoint);
        }

        if (jData.has(Constant.JSON.CALL_WAIT_ID)) {
            String callWaitId = jData.getString(Constant.JSON.CALL_WAIT_ID);
            result.put(WAITING_CALL_ID, callWaitId);
        }
        return result;
    }
}
