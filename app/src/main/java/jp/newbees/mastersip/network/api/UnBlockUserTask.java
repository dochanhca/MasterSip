package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 5/25/17.
 */

public class UnBlockUserTask extends BaseTask<Void> {

    private String destUserId;

    public UnBlockUserTask(Context context, String destUserId) {
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
        return Constant.API.UNBLOCk;
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
