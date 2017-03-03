package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 3/3/17.
 */

public class RegisterFCMTask extends BaseTask {

    private String extensionId;
    private String registrationId;

    public RegisterFCMTask(Context context, String extensionId, String registrationId) {
        super(context);
        this.extensionId = extensionId;
        this.registrationId = registrationId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.EXTENSION_ID, extensionId);
        jParam.put(Constant.JSON.OS, Constant.Application.ANDROID);
        jParam.put(Constant.JSON.REGISTRATION_ID, registrationId);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.REGISTER_PUSH_NOTIFY;
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
