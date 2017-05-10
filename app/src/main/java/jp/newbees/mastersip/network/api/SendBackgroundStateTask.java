package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 5/10/17.
 */

public class SendBackgroundStateTask extends BaseTask {
    private int status;

    public SendBackgroundStateTask(Context context, int status) {
        super(context);
        this.status = status;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.EXTENSION, ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension());
        jsonObject.put(Constant.JSON.STATUS, status);
        return jsonObject;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHANGE_BACKGROUND_STATE;
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
