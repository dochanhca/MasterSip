package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 5/23/17.
 */

public class DownloadImageTask extends BaseTask<Void> {

    private int imageId;
    private int type;

    public DownloadImageTask(Context context, int imageId, int type) {
        super(context);
        this.imageId = imageId;
        this.type = type;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        jParam.put(Constant.JSON.IMAGE_ID, imageId);
        jParam.put(Constant.JSON.IMAGE_TYPE, type);
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.DOWN_IMAGE;
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
