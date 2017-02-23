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

public class ChangePasswordTask extends BaseTask {

    private String email;
    private String newPass;
    private String code;

    public ChangePasswordTask(Context context, String email, String newPass, String code) {
        super(context);
        this.email = email;
        this.newPass = newPass;
        this.code = code;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.CODE, code);
        jParam.put(Constant.JSON.EMAIL, email);
        jParam.put(Constant.JSON.NEW_PASS, this.newPass);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHANGE_PASS;
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
