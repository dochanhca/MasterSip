package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/23/17.
 */

public class DeleteImageTask extends BaseTask<Boolean> {
    private final UserItem userItem;
    private final ImageItem imageItem;

    public DeleteImageTask(Context context, UserItem userItem, ImageItem imageItem) {
        super(context);
        this.userItem = userItem;
        this.imageItem = imageItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.USER_ID, userItem.getUserId());
        jParams.put(Constant.JSON.IMG_ID, imageItem.getImageId());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.DELETE_IMAGE;
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
