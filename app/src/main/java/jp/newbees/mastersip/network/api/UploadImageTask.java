package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

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
        JSONObject jData = data.getJSONObject(Constant.JSON.kData);

        ImageItem imageItem = new ImageItem();
        imageItem.setImageId(jData.getInt(Constant.JSON.kImageId));
        imageItem.setOriginUrl(jData.getString(Constant.JSON.kImageUrl));
        imageItem.setThumbUrl(jData.getString(Constant.JSON.kImageThumbUrl));

        return imageItem;
    }

    @Override
    public String genURL() {
        return Constant.API.UPLOAD_IMAGE;
    }

    @Override
    public int genMethod() {
        return Request.Method.POST;
    }

    @Override
    protected InputStream getInputStream() {
        return inputStream;
    }

    @Override
    protected String getFileName() {
        String fileName = "android_" + System.currentTimeMillis() + ".jpg";
        return fileName;
    }

    @Nullable
    @Override
    protected HashMap<String, Integer> genBodyParam() {
        HashMap<String, Integer> params = new HashMap<>();
        params.put(Constant.JSON.kUserId, Integer.valueOf(userId));
        params.put(Constant.JSON.kUploadType, typeUpload);
        return params;
    }
}
