package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/10/17.
 */

public class CheckIncomingCallTask extends BaseTask<Map<String, Object>> {

    private String receiverExtension;

    public static final String INCOMING_CALL_TYPE = "INCOMING_CALL_TYPE";
    public static final String CALL_ID = "CALL ID";
    public static final String CALLER = "CALLER";

    /**
     * This API use for check incoming call
     * @param context
     * @param receiverExtension
     */
    public CheckIncomingCallTask(Context context, String receiverExtension) {
        super(context);
        this.receiverExtension = receiverExtension;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.RECEIVER, receiverExtension);
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHECK_TYPE_INCOMING_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Map<String, Object> didResponse(JSONObject data) throws JSONException {
        Logger.e(TAG, "Data from check call " + data.toString());
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        JSONObject jCaller = jData.getJSONObject(Constant.JSON.CALLER);

        int inComingCallType = jData.getInt(Constant.JSON.TYPE);
        UserItem caller = getCaller(jCaller);
        String callID = jData.getInt(Constant.JSON.CALL_ID) + "";

        Map<String, Object> result = new HashMap<>();
        result.put(INCOMING_CALL_TYPE, inComingCallType);
        result.put(CALLER, caller);
        result.put(CALL_ID, callID);
        return result;
    }

    private UserItem getCaller(JSONObject jCaller) throws JSONException {
        UserItem caller = new UserItem();
        String extensionId = jCaller.getString(Constant.JSON.EXTENSION_ID);
        String handleName = jCaller.getString(Constant.JSON.HANDLE_NAME);

        SipItem sipItem = new SipItem(extensionId);
        if (!jCaller.isNull(Constant.JSON.URL_AVATAR)) {
            String urlAvatar = jCaller.getString(Constant.JSON.URL_AVATAR);
            ImageItem avatar = new ImageItem();
            avatar.setOriginUrl(urlAvatar);
            caller.setAvatarItem(avatar);
        }
        caller.setSipItem(sipItem);
        caller.setUsername(handleName);
        int gender = ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE ?
                UserItem.FEMALE : UserItem.MALE;
        caller.setGender(gender);
        return caller;
    }
}
