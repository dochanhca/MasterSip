package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/19/17.
 */

public class LogoutTask extends BaseTask {

    private final UserItem userItem;

    public LogoutTask(Context context, UserItem userItem) {
        super(context);
        this.userItem = userItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.USER_ID, userItem.getUserId());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.LOGOUT;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Object didResponse(JSONObject data) throws JSONException {
        return new Object();
    }
}
