package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

/**
 * Created by vietbq on 2/2/17.
 */

public class GetListRoomTask extends BaseTask<Map<String, Object>> {

    public static final String NUMBER_OF_ROOM_UNREAD = "NUMBER_OF_ROOM_UNREAD";
    public static final String LIST_ROOM_CHAT = "LIST_ROOM_CHAT";
    private final UserItem userItem;
    private int lastRoomId;

    /**
     * This API uses for get list rooms
     * @param context
     * @param userItem
     * @param lastRoomId
     */
    public GetListRoomTask(Context context, UserItem userItem, int lastRoomId) {
        super(context);
        this.userItem = userItem;
        this.lastRoomId = lastRoomId;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        if (lastRoomId != 0) {
            jParams.put(Constant.JSON.LAST_ROOM_ID, lastRoomId);
        }
        jParams.put(Constant.JSON.USER_ID, userItem.getUserId());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.LIST_ROOM + "/" + userItem.getUserId() + "/list";
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected Map<String, Object> didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        int numberOfRoomUnread = jData.getInt(Constant.JSON.TOTAL_UNREAD);
        JSONArray arrRoomChat = jData.getJSONArray(Constant.JSON.LIST_CHAT_ROOMS);
        List<RoomChatItem> roomChatItems = JSONUtils.parseListRoomChat(arrRoomChat);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NUMBER_OF_ROOM_UNREAD, numberOfRoomUnread);
        result.put(LIST_ROOM_CHAT, roomChatItems);
        return result;
    }
}
