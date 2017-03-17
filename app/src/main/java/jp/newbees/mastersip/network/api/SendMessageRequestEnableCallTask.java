package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

import static jp.newbees.mastersip.utils.Constant.API.REQUEST_ENABLE_VOICE_CALL;

/**
 * Created by thangit14 on 2/10/17.
 */
public class SendMessageRequestEnableCallTask extends BaseTask<SendMessageRequestEnableCallTask.Type> {

    public enum Type {
        VOICE, VIDEO, VIDEO_CHAT
    }

    private Type type;
    private UserItem userItem;
    private Context context;

    public SendMessageRequestEnableCallTask(Context context, UserItem userItem, Type type) {
        super(context);
        this.context = context;
        this.userItem = userItem;
        this.type = type;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.DIS_EXTENSION, userItem.getSipItem().getExtension());
        jParams.put(Constant.JSON.MESSAGE, getMessage());
        return jParams;
    }

    private String getMessage() {
        String message = "";
        switch (type) {
            case VOICE:
                message = String.format(context.getString(R.string.message_request_enable_voice), userItem.getUsername());
                break;
            case VIDEO:
                message = String.format(context.getString(R.string.message_request_enable_video), userItem.getUsername());
                break;
            case VIDEO_CHAT:
                message = String.format(context.getString(R.string.message_request_enable_video_chat), userItem.getUsername());
                break;
            default:
                break;
        }
        return message;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return REQUEST_ENABLE_VOICE_CALL;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected Type didResponse(JSONObject data) throws JSONException {
        return type;
    }
}
