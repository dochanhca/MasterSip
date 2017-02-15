package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 2/15/17.
 */

public class ResetPasswordTask extends BaseTask {

    private String email;

    public ResetPasswordTask(Context context, String email) {
        super(context);
        this.email = email;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.EMAIL, email);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.FORGOT_PASS;
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
