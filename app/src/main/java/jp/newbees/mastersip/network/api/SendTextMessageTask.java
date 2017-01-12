package jp.newbees.mastersip.network.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;

import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_TEXT;

/**
 * Created by vietbq on 1/4/17.
 */

public class SendTextMessageTask extends BaseTask<BaseChatItem> {

    private final TextChatItem textChatItem;
    private final UserItem sender;

    public SendTextMessageTask(Context context, TextChatItem textChatItem) {
        super(context);
        this.textChatItem = textChatItem;
        this.sender = this.textChatItem.getSender();
    }

    @Nullable
    @Override
    protected JSONObject genParams() throws JSONException {
        String message = "";
        try {
            message = URLEncoder.encode(this.textChatItem.getMessage(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jParams = new JSONObject();
        jParams.put(Constant.JSON.kType, CHAT_TEXT);
        jParams.put(Constant.JSON.kContent , message);
        jParams.put(Constant.JSON.kExtensionSource, this.textChatItem.getSender().getSipItem().getExtension());
        jParams.put(Constant.JSON.kExtensionDestination, this.textChatItem.getSendee().getSipItem().getExtension());
        jParams.put(Constant.JSON.kRoomType, this.textChatItem.getRoomType());
        return jParams;
    }

    @NonNull
    @Override
    protected String getUrl() {
        return Constant.API.CHAT_MESSAGE;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    protected BaseChatItem didResponse(JSONObject data) throws JSONException {
        JSONObject jData = data.getJSONObject(Constant.JSON.DATA);
        JSONObject jResponse = jData.getJSONObject(Constant.JSON.kResponse);
        BaseChatItem chatItem = JSONUtils.parseChatItem(jResponse,this.sender);
        return chatItem;
    }
}
