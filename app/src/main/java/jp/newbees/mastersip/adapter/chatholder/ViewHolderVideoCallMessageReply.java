package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.VideoCallChatItem;

/**
 * Created by ducpv on 3/15/17.
 */

public class ViewHolderVideoCallMessageReply extends BaseChatReplyViewHolder<VideoCallChatItem> {
    private TextView txtTime;

    public ViewHolderVideoCallMessageReply(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
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
