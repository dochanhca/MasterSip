package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 2/3/17.
 */

public class SendGiftTask extends BaseTask<GiftChatItem> {

    private final UserItem userItem;
    private final GiftItem giftItem;

    public SendGiftTask(Context context, UserItem userItem, GiftItem giftItem) {
        super(context);
        this.userItem = userItem;
        this.giftItem = giftItem;
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.USER_ID, userItem.getUserId());
        jParams.put(Constant.JSON.GIFT_ID, giftItem.getGiftId());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.SEND_GIFT;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    protected GiftChatItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        int roomId = jData.getInt(Constant.JSON.ROOM_ID);
        int messageId = jData.getInt(Constant.JSON.MESSAGE_ID);
        int chatType = jData.getInt(Constant.JSON.TYPE);
        String date = jData.getString(Constant.JSON.DATE);
        int roomType = jData.getInt(Constant.JSON.ROOM_TYPE);

        GiftChatItem giftChatItem = new GiftChatItem();
        giftChatItem.setRoomId(roomId);
        giftChatItem.setMessageId(messageId);
        giftChatItem.setChatType(chatType);
        giftChatItem.setRoomType(roomType);
        giftChatItem.setFullDate(date);
        return giftChatItem;
    }
}
