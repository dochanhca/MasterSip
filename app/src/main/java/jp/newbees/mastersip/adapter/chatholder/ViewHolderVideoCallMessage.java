package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.VideoCallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderVideoCallMessage extends BaseChatViewHolder<VideoCallChatItem> {
    private TextView txtTime;

    public ViewHolderVideoCallMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        txtTime = (TextView) root.findViewById(R.id.txt_time);

    }

    @Override
    public void bindView(VideoCallChatItem videoCallChatItem) {
        txtTime.setText(videoCallChatItem.getShortDate());
    }
}
