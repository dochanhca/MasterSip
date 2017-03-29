package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

import static jp.newbees.mastersip.utils.JSONUtils.getMembers;


/**
 * Created by thangit14 on 1/17/17.
 */
public class GetChatHistoryTask extends BaseTask<LoadChatHistoryResultItem> {
    private Context context;
    private String userID;
    private String friendUserId;
    private int lastMessageId;

    public GetChatHistoryTask(Context context, String userID, String friendUserId, int lastMessageId) {
        super(context);
        this.context = context;
        this.userID = userID;
        this.friendUserId = friendUserId;
        this.lastMessageId = lastMessageId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        return null;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHAT_HISTORY + "/" + userID + "/" + friendUserId + "/" + lastMessageId;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected LoadChatHistoryResultItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        HashMap<String, UserItem> members = getMembers(jData.getJSONArray(Constant.JSON.MEMBERS));
        ArrayList<BaseChatItem> baseChatItems = JSONUtils.parseChatHistory(jData, members, context);
        return new LoadChatHistoryResultItem(members, baseChatItems);
    }
}