package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/14/16.
 */

public class UploadImageTask extends BaseUploadTask<ImageItem> {

    public static int UPLOAD_FOR_AVATAR = 3;
    public static int UPLOAD_FOR_GALLERY = 2;
    public static int UPLOAD_FOR_POST = 1;
    public static int UPLOAD_FOR_TEMPLATE = 0;

    private final String userId;
    private final int typeUpload;
    private InputStream inputStream;

    public UploadImageTask(Context mContext, String userId, int typeUpload, InputStream inputStream) {
        super(mContext);
        this.userId = userId;
        this.typeUpload = typeUpload;
        this.inputStream = inputStream;
    }

    @Override
    protected String getNameEntity() {
        return "file";
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
        return Constant.API.UPLOAD_IMAGE;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected InputStream getInputStream() {
        return inputStream;
    }

    @Override
    protected String getFileName() {
        return "android_" + System.currentTimeMillis() + ".jpg";
    }

    @Nullable
    @Override
    protected Map<String, Object> genBodyParam() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.JSON.USER_ID, userId);
        params.put(Constant.JSON.UPLOAD_TYPE, typeUpload);
        return params;
    }
}
