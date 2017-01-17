package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/16/17.
 */

public class LoginFacebookTask extends BaseTask<UserItem> {

    private final UserItem userItem;

    public LoginFacebookTask(Context context, UserItem userItem) {
        super(context);
        this.userItem = userItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        String deviceId = ConfigManager.getInstance().getDeviceId();
        String facebookId = userItem.getFacebookId();
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.SECRET_KEY, "AUTOGENCONFIGINFILE");
        jParams.put(Constant.JSON.DEVICE_ID, deviceId);
        jParams.put(Constant.JSON.SOCIAL_ID, facebookId);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.LOGIN_FACEBOOK_URL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected UserItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);

        String extension = jData.getString(Constant.JSON.K_EXTENSION);
        String password = jData.getString(Constant.JSON.PASSWORD);
        String userId = jData.getString(Constant.JSON.USER_ID);
        String registerToken = jData.getString(Constant.JSON.REGIST_TOKEN);

        SipItem sipItem = new SipItem(extension,password);
        this.userItem.setSipItem(sipItem);
        this.userItem.setUserId(userId);

        ConfigManager.getInstance().saveRegisterToken(registerToken);
        ConfigManager.getInstance().saveAuthId(userId);
        ConfigManager.getInstance().saveUser(userItem);

        return userItem;
    }

    public final UserItem getUserItem() {
        return userItem;
    }
}
