package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.CallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderCallMessage extends BaseChatViewHolder<CallChatItem> {
    private TextView txtTime;
    private TextView txtCallType;
    private TextView txtDuration;
    private ImageView imgCall;

    public ViewHolderCallMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtCallType = (TextView) root.findViewById(R.id.txt_call_type);
        txtDuration = (TextView) root.findViewById(R.id.txt_duration);
        imgCall = (ImageView) root.findViewById(R.id.img_call);
    }

    @Override
    public void bindView(CallChatItem callChatItem) {
        txtTime.setText(callChatItem.getShortDate());
        txtDuration.setText("".equals(getCallDuration(callChatItem))
                ? getContext().getString(R.string.no_answer) : getCallDuration(callChatItem));
        txtCallType.setText(getCallType(callChatItem.getChatType()));

        int drawableId = callChatItem.getCallType() == BaseChatItem.CallType.END_CALL
                ? R.drawable.ic_call_history : R.drawable.ic_call_history_missed;
        imgCall.setImageDrawable(getContext().getResources().getDrawable(drawableId));
    }

    private String getCallDuration(CallChatItem callChatItem) {
        String text;
        switch (callChatItem.getCallType()) {
            case BaseChatItem.CallType.CANCEL_CALL:
                text = getContext().getString(R.string.cancel);
                break;
            case BaseChatItem.CallType.MISS_CALL:
            case BaseChatItem.CallType.BUSY_CALL:
                text = getContext().getString(R.string.no_answer);
                break;
            default:
                text = callChatItem.getDuration();
                break;
        }
        return text;
    }
}
