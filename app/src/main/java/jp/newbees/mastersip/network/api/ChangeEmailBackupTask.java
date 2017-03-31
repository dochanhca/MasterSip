package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 2/14/17.
 */
public class ChangeEmailBackupTask extends BaseTask<Void>{
    private EmailBackupItem item;

    /**
     * This API uses for setup email backup
     * @param context
     * @param item
     */
    public ChangeEmailBackupTask(Context context, EmailBackupItem item) {
        super(context);
        this.item = item;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return JSONUtils.genParamsToChangeEmailBackup(item);
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHANGE_EMAIL_BACKUP;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
