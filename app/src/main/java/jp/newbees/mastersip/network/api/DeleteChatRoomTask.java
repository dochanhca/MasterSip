package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 2/23/17.
 */

public class DeleteChatRoomTask extends BaseTask {

    private List<Integer> roomChatIds;

    /**
     * Delete All List
     * @param context
     */
    public DeleteChatRoomTask(Context context) {
        super(context);
    }

    /**
     * Delete List room with id
     * @param context
     * @param roomChatIds
     */
    public DeleteChatRoomTask(Context context, List<Integer> roomChatIds) {
        super(context);
        this.roomChatIds = roomChatIds;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParam = new JSONObject();
        if (roomChatIds == null) {
            jParam.put(Constant.JSON.ALL, 1);
        } else {
            JSONArray arrChatRoomIds = new JSONArray(Arrays.asList(roomChatIds));
            jParam.put(Constant.JSON.ARR_CHAT_ROOM_ID, arrChatRoomIds);
        }
        return jParam;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.DELETE_CHAT_ROOM;
    }

    @Override
    protected int getMethod() {
        return Request.Method.DELETE;
    }

    @Override
    protected Object didResponse(JSONObject data) throws JSONException {
        return null;
    }
}
