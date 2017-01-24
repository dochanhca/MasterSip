package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/20/17.
 */

public class MyPhotosTask extends BaseTask<GalleryItem> {

    private final GalleryItem galleryItem;

    public MyPhotosTask(Context context, GalleryItem galleryItem) {
        super(context);
        this.galleryItem = galleryItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.PAGINATE, 40);
        jParams.put(Constant.JSON.IMAGE_ID, galleryItem.getNextId());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.LIST_MY_PHOTOS;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected GalleryItem didResponse(JSONObject data) throws JSONException {
        return new GalleryItem();
    }
}
