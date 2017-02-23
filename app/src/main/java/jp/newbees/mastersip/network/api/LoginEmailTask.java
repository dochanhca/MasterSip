package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 12/12/16.
 */

public class LoginEmailTask extends BaseTask<UserItem> {

    private final String email;
    private final String password;

    public LoginEmailTask(Context context, String email, String password) {
        super(context);
        this.email = email;
        this.password = password;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        String deviceId = getDeviceId();
        JSONObject jData = new JSONObject();
        jData.put(Constant.JSON.DEVICE_ID, deviceId);
        jData.put(Constant.JSON.PASSWORD, this.password);
        jData.put(Constant.JSON.EMAIL, this.email);
        return jData;
    }

    @Override
    protected String getUrl() {
        return Constant.API.LOGIN_BY_EMAIL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected UserItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        UserItem userItem = JSONUtils.parseUserLoginWithEmail(jData);
        userItem.setEmail(email);
        ConfigManager.getInstance().saveUser(userItem);
        return userItem;
    }
}
