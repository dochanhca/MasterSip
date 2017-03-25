package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/23/17.
 */

public class UploadFileForChatTask extends BaseUploadTask<BaseChatItem> {

    private String receiverExtension;
    private UserItem sender;
    private int typeUpload;
    private InputStream fileUpload;
    private Context context;

    public UploadFileForChatTask(Context context, String receiverExtension, UserItem sender,
                                 int typeUpload, InputStream fileUpload) {
        super(context);
        this.context = context;
        this.receiverExtension = receiverExtension;
        this.sender = sender;
        this.typeUpload = typeUpload;
        this.fileUpload = fileUpload;
    }

    @Override
    protected String getNameEntity() {
        return "file";
    }

    @Override
    protected BaseChatItem didResponse(JSONObject data) throws JSONException {
        Logger.e(TAG, data.toString());
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        return JSONUtils.parseChatItem(jData, sender);
    }

    @Override
    public String getUrl() {
        return Constant.API.SAVE_FILE_CHAT;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected InputStream getInputStream() {
        return fileUpload;
    }

    @Override
    protected String getFileName() {
        String fileName = null;

        if (typeUpload == Constant.API.TYPE_UPLOAD_IMAGE) {
            fileName = "android_" + System.currentTimeMillis() + ".jpg";
        }
        return fileName;
    }

    @Nullable
    @Override
    protected Map<String, Object> genBodyParam() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.JSON.EXTENSION_SRC, sender.getSipItem().getExtension());
        params.put(Constant.JSON.EXTENSION_DEST, receiverExtension);
        params.put(Constant.JSON.TYPE, typeUpload);
        return params;
    }
}
