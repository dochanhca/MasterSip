package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.CallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderVoiceCallMessage extends BaseChatViewHolder<CallChatItem> {
    private TextView txtTime;

    public ViewHolderVoiceCallMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtTime = (TextView) root.findViewById(R.id.txt_time);
    }

    @Override
    public void bindView(CallChatItem callChatItem) {
        txtTime.setText(callChatItem.getShortDate());
    }
}
