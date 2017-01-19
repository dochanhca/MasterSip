package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.MyMenuItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/19/17.
*/

public class MyProfileTask extends BaseTask<MyMenuItem> {
    public MyProfileTask(Context context) {
        super(context);
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
    protected MyMenuItem didResponse(JSONObject data) throws JSONException {
        MyMenuItem myMenuItem = new MyMenuItem();
        return myMenuItem;
    }
}
