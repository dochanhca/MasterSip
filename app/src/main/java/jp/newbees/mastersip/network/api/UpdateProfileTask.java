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
    protected JSONObject genBodyParam() throws JSONException {
        JSONObject jParams = new JSONObject();

        jParams.put(Constant.JSON.kRegisterToken,getRegisterToken());
        jParams.put(Constant.JSON.kBirthday,userItem.getDateOfBirth());
        jParams.put(Constant.JSON.kEmail,userItem.getEmail());
        jParams.put(Constant.JSON.kPassword,userItem.getSipItem().getSecret());
        jParams.put(Constant.JSON.kHandleName,userItem.getUsername());
        jParams.put(Constant.JSON.kPhoneNumber,"");
        jParams.put(Constant.JSON.kProvinceId,userItem.getLocation().getId());
        jParams.put(Constant.JSON.kAvatarId,userItem.getAvatarItem().getImageId());
        jParams.put(Constant.JSON.kJobId,userItem.getJobItem().getId());
        jParams.put(Constant.JSON.kDeviceId,getDeviceId());
        jParams.put(Constant.JSON.kTypeId,userItem.getTypeOfMan().getId());
        jParams.put(Constant.JSON.kTypeBoy,userItem.getTypeOfMan().getTitle());
        jParams.put(Constant.JSON.kCharmPoint,userItem.getCharmingPoint());
        jParams.put(Constant.JSON.kFreeTime,userItem.getAvailableTimeItem().getId());
        jParams.put(Constant.JSON.kSlogan, userItem.getMemo());

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
