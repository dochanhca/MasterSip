package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 1/24/17.
 */

public class UnFollowUserTask extends BaseTask {
    private String destUserId;

    public UnFollowUserTask(Context context, String destUserId) {
        super(context);
        this.destUserId = destUserId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.DEST_USER_ID, destUserId);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.UN_FOLLOW;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Object didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
