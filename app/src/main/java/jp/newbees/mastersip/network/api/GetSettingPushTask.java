package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.SettingPushItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 5/11/17.
 */

public class GetSettingPushTask extends BaseTask<SettingPushItem> {

    public GetSettingPushTask(Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return null;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.PUSH_NOTIFICATION;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected SettingPushItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parsePushNotifySetting(jData);
    }
}
