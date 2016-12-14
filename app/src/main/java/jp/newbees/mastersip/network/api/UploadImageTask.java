package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/14/16.
 */

public class UploadImageTask extends BaseUploadTask<ImageItem> {

    public static int UPLOAD_FOR_AVATAR   = 3;
    public static int UPLOAD_FOR_GALLERY  = 2;
    public static int UPLOAD_FOR_POST     = 1;
    public static int UPLOAD_FOR_TEMPLATE = 0;

    private final UserItem userItem;
    private final int typeUpload;

    protected UploadImageTask(Context mContext, UserItem userItem, int typeUpload) {
        super(mContext);
        this.userItem = userItem;
        this.typeUpload = typeUpload;
    }

    @Override
    protected String getNameEntity() {
        return "file";
    }

    @Override
    protected ImageItem didResponse(JSONObject data) throws JSONException {
        return new ImageItem();
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
        return null;
    }

    @Override
    protected String getFileName() {
        String fileName = "android_"+System.currentTimeMillis();
        return fileName;
    }

    @Nullable
    @Override
    protected HashMap<String, String> genBodyParam() {
        HashMap<String,String> params = new HashMap<>();
        params.put(Constant.JSON.kUserId,userItem.getUserId());
        params.put(Constant.JSON.kUploadType,""+typeUpload);
        return params;
    }
}
