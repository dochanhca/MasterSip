package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 4/5/17.
 */

public class SendDirectMessageTask extends BaseTask<Boolean> {

    private final String message;
    private final String toExtension;

    public SendDirectMessageTask(Context context, String toExtension, String message) {
        super(context);
        this.toExtension = toExtension;
        this.message = message;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.EXTENSION, toExtension);
        jParams.put(Constant.JSON.MESSAGE, this.message);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.SEND_DIRECT_MESSAGE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Boolean didResponse(JSONObject data) throws JSONException {
        return Boolean.TRUE;
    }
}
