package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by vynv on 5/16/17.
 */

public class ViewHolderTextMessageReplyDeleted extends BaseChatReplyViewHolder<BaseChatItem> {
    private TextView txtContent;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderTextMessageReplyDeleted(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
    }

    @Override
    protected void initView(View root) {
        txtContent = (TextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        setFriendAvatarClickListener(imgAvatar);
    }

    @Override
    public void bindView(BaseChatItem baseChatItem) {
        txtTime.setText(baseChatItem.getShortDate());
        txtContent.setText(getContext().getString(R.string.message_receiver_not_good));
        setUserAvatar(imgAvatar, baseChatItem);
    }
}
