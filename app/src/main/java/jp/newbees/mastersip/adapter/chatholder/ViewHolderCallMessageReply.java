package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.CallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderCallMessageReply extends BaseChatReplyViewHolder<CallChatItem> {
    private TextView txtTime;
    private TextView txtCallType;
    private TextView txtDuration;
    private ImageView imgCall;
    private ImageView imgAvatar;

    public ViewHolderCallMessageReply(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
    }

    @Override
    protected void initView(View root) {
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtCallType = (TextView) root.findViewById(R.id.txt_call_type);
        txtDuration = (TextView) root.findViewById(R.id.txt_duration);
        imgCall = (ImageView) root.findViewById(R.id.img_call);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        setFriendAvatarClickListener(imgAvatar);
    }

    @Override
    public void bindView(CallChatItem callChatItem) {
        txtTime.setText(callChatItem.getShortDate());
        txtDuration.setText(!"".equals(callChatItem.getDuration()) ?
                callChatItem.getDuration() : getContext().getString(R.string.missed_call));
        txtCallType.setText(getCallType(callChatItem.getChatType()));

        int drawableId = callChatItem.getCallType() == BaseChatItem.CallType.END_CALL
                ? R.drawable.ic_call_history_reply : R.drawable.ic_call_history_missed_reply;
        imgCall.setImageDrawable(getContext().getResources().getDrawable(drawableId));

        setUserAvatar(imgAvatar, callChatItem);
    }

    private String getCallType(int chatType) {
        String result = "";
        switch (chatType) {
            case BaseChatItem.ChatType.CHAT_VOICE_CALL:
                result = getContext().getString(R.string.voice_call);
                break;
            case BaseChatItem.ChatType.CHAT_VIDEO_CALL:
                result = getContext().getString(R.string.video_call);
                break;
            case BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL:
                result = getContext().getString(R.string.video_chat_call);
                break;
            default:
                break;
        }
        return result;
    }
}