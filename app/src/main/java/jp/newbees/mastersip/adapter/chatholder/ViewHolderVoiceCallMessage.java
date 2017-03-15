package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.VoiceCallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderVoiceCallMessage extends BaseChatViewHolder<VoiceCallChatItem> {
    private TextView txtTime;

    public ViewHolderVoiceCallMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtTime = (TextView) root.findViewById(R.id.txt_time);
    }

    @Override
    public void bindView(VoiceCallChatItem voiceCallChatItem) {
        txtTime.setText(voiceCallChatItem.getShortDate());
    }
}
