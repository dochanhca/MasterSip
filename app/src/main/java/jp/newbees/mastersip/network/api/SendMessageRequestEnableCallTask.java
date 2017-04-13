package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

import static jp.newbees.mastersip.utils.Constant.API.REQUEST_ENABLE_VOICE_CALL;

/**
 * Created by thangit14 on 2/10/17.
 */
public class SendMessageRequestEnableCallTask extends BaseTask<Integer> {

    private int type;
    private UserItem userItem;

    public SendMessageRequestEnableCallTask(Context context, UserItem userItem, int type) {
        super(context);
        this.userItem = userItem;
        this.type = type;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.DIS_EXTENSION, userItem.getSipItem().getExtension());
        jParams.put(Constant.JSON.TYPE, type);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return REQUEST_ENABLE_VOICE_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Integer didResponse(JSONObject data) throws JSONException {
        return type;
    }
}
