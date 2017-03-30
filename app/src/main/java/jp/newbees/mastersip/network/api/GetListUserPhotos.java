package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 1/19/17.
 */

public class GetListUserPhotos extends BaseTask<GalleryItem> {

    private int imageId;
    private String userId;

    /**
     * This API uses for get list photos of user
     * @param context
     * @param imageId
     * @param userId
     */
    public GetListUserPhotos(Context context, int imageId, String userId) {
        super(context);
        this.imageId = imageId;
        this.userId = userId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        if (imageId > 0) {
            JSONObject jParam = new JSONObject();
            jParam.put(Constant.JSON.IMAGE_ID, imageId);
            return jParam;
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.JSON.USER + "/" + userId + "/" + Constant.API.PHOTOS;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected GalleryItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseGallery(jData);
    }
}
