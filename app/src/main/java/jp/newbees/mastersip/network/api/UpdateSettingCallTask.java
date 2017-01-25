package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/25/17.
 */

public class UpdateSettingCallTask extends BaseTask<SettingItem> {
    private final SettingItem settingItem;

    public UpdateSettingCallTask(Context context, SettingItem settingItem) {
        super(context);
        this.settingItem = settingItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.VIDEO_CALL_SET, settingItem.getVideoCall());
        jParams.put(Constant.JSON.VOICE_CALL_SET, settingItem.getVoiceCall());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.SETTING_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected SettingItem didResponse(JSONObject data) throws JSONException {
        return settingItem;
    }
}
