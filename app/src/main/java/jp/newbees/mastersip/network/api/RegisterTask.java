package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

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
    protected JSONObject genBodyParam() throws JSONException {
        JSONObject jParam = new JSONObject();
        String dob = userItem.getDateOfBirth();
        Number gender = new Integer(userItem.getGender());

        String facebooklID = userItem.getFacebookId();
        String deviceId = ConfigManager.getInstance().getDeviceId();
        String osVersion = ConfigManager.getInstance().getOSVersion();
        String appVersion = ConfigManager.getInstance().getApplicationVersion();
        String deviceInfo = ConfigManager.getInstance().getDeviceInfo();

        jParam.put(Constant.JSON.kBirthday,dob);
        jParam.put(Constant.JSON.kGender,gender);
        jParam.put(Constant.JSON.kSocialId,facebooklID);
        jParam.put(Constant.JSON.kDeviceId,deviceId);
        jParam.put(Constant.JSON.kOSVersion,osVersion);
        jParam.put(Constant.JSON.kAppVersion,appVersion);
        jParam.put(Constant.JSON.kDeviceInfo,deviceInfo);

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
        Logger.d(TAG,data.toString());
        return this.userItem;
    }
}
