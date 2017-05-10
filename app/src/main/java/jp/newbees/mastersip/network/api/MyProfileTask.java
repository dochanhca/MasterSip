package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 1/19/17.
*/

public class MyProfileTask extends BaseTask<UserItem> {
    private Context context;

    public MyProfileTask(Context context) {
        super(context);
        this.context = context;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return new JSONObject();
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.MY_PROFILE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected UserItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        UserItem userItem = JSONUtils.parseMyMenuItem(context, jData);
        ConfigManager.getInstance().saveUser(userItem);
        return userItem;
    }
}
