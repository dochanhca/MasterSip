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

    public static final String NEXT_PAGE = "NEXT_PAGE";
    public static final String NUMBER_OF_ROOM_UNREAD = "NUMBER_OF_ROOM_UNREAD";
    public static final String LIST_ROOM_CHAT = "LIST_ROOM_CHAT";
    private final UserItem userItem;
    private final String page;

    public GetListRoomTask(Context context, UserItem userItem, String page) {
        super(context);
        this.userItem = userItem;
        this.page = page;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.PAGE, page);
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
        String nextPage = jData.getString(Constant.JSON.NEXT_PAGE);
        int numberOfRoomUnread = jData.getInt(Constant.JSON.COUNT_ROOM_UNREAD);
        JSONArray arrRoomChat = jData.getJSONArray(Constant.JSON.LIST_CHAT_ROOMS);
        List<RoomChatItem> roomChatItems = JSONUtils.parseListRoomChat(arrRoomChat);
        HashMap<String, Object> result = new HashMap<>();
        result.put(NEXT_PAGE, nextPage);
        result.put(NUMBER_OF_ROOM_UNREAD, numberOfRoomUnread);
        result.put(LIST_ROOM_CHAT, roomChatItems);
        return result;
    }
}
