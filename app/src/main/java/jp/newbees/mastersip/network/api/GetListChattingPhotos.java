package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by ducpv on 2/9/17.
 */

public class GetListChattingPhotos extends BaseTask<ChattingGalleryItem> {

    private String nextId;
    private String userId;

    /**
     * This API uses for get list photos in a chat room
     * @param context
     * @param nextId
     * @param userId
     */
    public GetListChattingPhotos(Context context, String nextId, String userId) {
        super(context);
        this.nextId = nextId;
        this.userId = userId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        if (!"".equals(nextId)) {
            JSONObject jParam = new JSONObject();
            jParam.put(Constant.JSON.NEXT_ID, nextId);
            return jParam;
        }
        return null;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHATTING_PHOTO +"/" + userId;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected ChattingGalleryItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseChattingGallery(jData);
    }
}
