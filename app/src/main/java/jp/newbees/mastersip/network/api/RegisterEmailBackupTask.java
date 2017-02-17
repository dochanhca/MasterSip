package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by thangit14 on 2/14/17.
 */
public class RegisterEmailBackupTask extends BaseTask<Void>{
    private EmailBackupItem emailBackupItem;

    public RegisterEmailBackupTask(Context context, EmailBackupItem emailBackupItem) {
        super(context);
        this.emailBackupItem = emailBackupItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return JSONUtils.genParamsToRegisterEmailBackup(emailBackupItem);
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.UPDATE_USER_PASS;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Void didResponse(JSONObject data) throws JSONException {
        ConfigManager.getInstance().saveBackupEmail(emailBackupItem.getEmail());
        return null;
    }
}
