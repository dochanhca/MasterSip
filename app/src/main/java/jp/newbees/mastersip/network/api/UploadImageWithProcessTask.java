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
 * Created by vietbq on 1/23/17.
 */

public class UploadImageWithProcessTask extends BaseUploadV2Task<ImageItem> {

    public static int UPLOAD_FOR_AVATAR = 3;
    public static int UPLOAD_FOR_GALLERY = 2;
    public static int UPLOAD_FOR_POST = 1;
    public static int UPLOAD_FOR_TEMPLATE = 0;

    private final String userId;
    private final int typeUpload;
    private final String filePath;

    public UploadImageWithProcessTask(Context context, String userId, int typeUpload, String filePath) {
        super(context);
        this.userId = userId;
        this.typeUpload = typeUpload;
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
        imageItem.setImageType(this.typeUpload);
        return imageItem;
    }

    @Override
    public String getUrl() {
        return Constant.API.UPLOAD_IMAGE;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Nullable
    @Override
    protected Map<String, Object> genBodyParam() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.JSON.USER_ID, userId);
        params.put(Constant.JSON.UPLOAD_TYPE, typeUpload);
        return params;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }
}