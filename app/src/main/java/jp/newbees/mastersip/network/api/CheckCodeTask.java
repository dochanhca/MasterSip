package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 2/14/17.
 */
public class CheckCodeTask extends BaseTask<Void>{
    private String code;

    /**
     * This API uses for check Code forgot password
     * @param context
     * @param code
     */
    public CheckCodeTask(Context context, String code) {
        super(context);
        this.code = code;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return JSONUtils.genParamsToCheckCode(code);
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHECK_CODE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
