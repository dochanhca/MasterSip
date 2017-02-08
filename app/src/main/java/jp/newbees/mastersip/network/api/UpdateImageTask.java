package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 2/8/17.
 */

public class UpdateImageTask extends BaseUploadV2Task<ImageItem> {

    private int imageId;
    private String userId;
    private String filePath;

    public UpdateImageTask(Context context, int imageId, String userId, String filePath) {
        super(context);
        this.imageId = imageId;
        this.userId = userId;
        this.filePath = filePath;
    }

    @Override
    protected ImageItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        ImageItem imageItem = new ImageItem();
        imageItem.setImageId(jData.getInt(Constant.JSON.IMAGE_ID));
        imageItem.setOriginUrl(jData.getString(Constant.JSON.IMAGE_PATH_FULL));
        imageItem.setThumbUrl(jData.getString(Constant.JSON.IMAGE_PATH_THUMB));
        imageItem.setImageStatus(jData.getInt(Constant.JSON.IMAGE_STATUS));
        return imageItem;
    }

    @Override
    public String getUrl() {
        return Constant.API.UPDATE_IMAGE;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Nullable
    @Override
    protected Map<String, Object> genBodyParam() {
        Map<String, Object> jParam = new HashMap<>();
        jParam.put(Constant.JSON.USER_ID, userId);
        jParam.put(Constant.JSON.IMAGE_ID, imageId);
        return jParam;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }
}
