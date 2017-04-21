package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 4/21/17.
 */

public class GetOnlineListTask extends BaseTask<Map<String, Object>> {

    public static final String LIST_USER = "LIST_USER";
    public static final String NEXT = "NEXT";

    private int next;

    public GetOnlineListTask(Context context, int next) {
        super(context);
        this.next = next;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.NEXT, next);
        return jsonObject;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.ONLINE_LIST;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Map<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseOnlineList(jData);
    }
}
