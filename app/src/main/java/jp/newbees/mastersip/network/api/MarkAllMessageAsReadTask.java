package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 2/23/17.
 */

public class MarkAllMessageAsReadTask extends BaseTask {

    public MarkAllMessageAsReadTask(Context context) {
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
        return Constant.API.MARK_MESSAGE_AS_READ;
    }

    @Override
    protected int getMethod() {
        return Request.Method.PATCH;
    }

    @Override
    protected Object didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
