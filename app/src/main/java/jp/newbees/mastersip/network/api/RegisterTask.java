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
 * Created by vietbq on 12/12/16.
 */

public class RegisterTask extends BaseTask<UserItem> {

    private UserItem userItem;

    public RegisterTask(Context context, UserItem userItem) {
        super(context);
        this.userItem = userItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        String dob = userItem.getDateOfBirth();
        Number gender = new Integer(userItem.getGender());

        String facebooklID = userItem.getFacebookId();
        if (null == facebooklID || facebooklID.isEmpty()){
            facebooklID = "0";
        }
        String deviceId = ConfigManager.getInstance().getDeviceId();
        String osVersion = ConfigManager.getInstance().getOSVersion();
        String appVersion = ConfigManager.getInstance().getApplicationVersion();
        String deviceInfo = ConfigManager.getInstance().getDeviceInfo();

        jParam.put(Constant.JSON.BIRTHDAY,dob);
        jParam.put(Constant.JSON.GEN,gender);
        jParam.put(Constant.JSON.SOCIAL_ID,facebooklID);
        jParam.put(Constant.JSON.DEVICE_ID,deviceId);
        jParam.put(Constant.JSON.OS_VERSION,osVersion);
        jParam.put(Constant.JSON.APP_VERSION,appVersion);
        jParam.put(Constant.JSON.DEVICE_INFO,deviceInfo);
        jParam.put(Constant.JSON.PLATFORM, Constant.Application.ANDROID);

        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.REGISTER;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected UserItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);

        String extension = jData.getString(Constant.JSON.EXTENSION);
        String password = jData.getString(Constant.JSON.PASSWORD);
        String userId = jData.getString(Constant.JSON.USER_ID);
        String registerToken = jData.getString(Constant.JSON.REGIST_TOKEN);

        SipItem sipItem = new SipItem(extension,password);
        this.userItem.setSipItem(sipItem);
        this.userItem.setUserId(userId);

        ConfigManager.getInstance().saveRegisterToken(registerToken);
        ConfigManager.getInstance().saveAuthId(userId);
        ConfigManager.getInstance().saveUser(userItem);
        return this.userItem;
    }
}
