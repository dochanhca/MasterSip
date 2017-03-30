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

public class FollowUserTask extends BaseTask<Void> {
    private String destUserId;

    /**
     * This API uses for follow a user
     * @param context
     * @param destUserId
     */
    public FollowUserTask(Context context, String destUserId) {
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
        return Constant.API.FOLLOW;
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
