package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 5/12/17.
 */

public class SendFootPrintTask extends BaseTask<Void> {

    private String userId;

    public SendFootPrintTask(Context context, String userId) {
        super(context);
        this.userId = userId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.USER_ID, userId);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.FOOTPRINT;
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
