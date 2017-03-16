package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/15/16.
 */

public class UpdateProfileTask extends BaseTask<UserItem> {
    private final UserItem userItem;

    public UpdateProfileTask(Context context, UserItem userItem) {
        super(context);
        this.userItem = userItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();

        jParams.put(Constant.JSON.BIRTHDAY, userItem.getDateOfBirth());
        jParams.put(Constant.JSON.EMAIL, "");
        jParams.put(Constant.JSON.PASSWORD, userItem.getSipItem().getSecret());
        jParams.put(Constant.JSON.HANDLE_NAME, userItem.getUsername());
        jParams.put(Constant.JSON.PHONE_NUMBER, "");
        jParams.put(Constant.JSON.PROVINCE_ID, userItem.getLocation().getId());
        jParams.put(Constant.JSON.AVATAR_ID, userItem.getAvatarItem().getImageId());
        jParams.put(Constant.JSON.JOB_ID, userItem.getJobItem().getId());
        jParams.put(Constant.JSON.DEVICE_ID, getDeviceId());
        jParams.put(Constant.JSON.TYPE_ID, userItem.getTypeGirl().getId());
        jParams.put(Constant.JSON.TYPE_BOY, userItem.getTypeBoy());
        jParams.put(Constant.JSON.CHARM_POINT, userItem.getCharmingPoint());
        jParams.put(Constant.JSON.FREE_TIME, userItem.getAvailableTimeItem().getId());
        jParams.put(Constant.JSON.SLOGAN, userItem.getMemo());

        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.UPDATE_REGISTER_PROFILE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected UserItem didResponse(JSONObject data) throws JSONException {
        return this.userItem;
    }
}
