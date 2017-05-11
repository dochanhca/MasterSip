package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.SettingPushItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 5/11/17.
 */

public class SettingPushTask extends BaseTask<Void> {

    private SettingPushItem settingPushItem;

    public SettingPushTask(Context context, SettingPushItem settingPushItem) {
        super(context);
        this.settingPushItem = settingPushItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.ADMIN, settingPushItem.getAdmin());
        jParams.put(Constant.JSON.ALL_USER, settingPushItem.getAllUser());
        jParams.put(Constant.JSON.USER_FOLLOW, settingPushItem.getUserFollow());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.PUSH_NOTIFICATION;
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
