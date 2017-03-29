package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderTextMessageReply extends BaseChatReplyViewHolder<BaseChatItem>{
    private TextView txtContent;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderTextMessageReply(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
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
        TextChatItem textChatItem = (TextChatItem) baseChatItem;
        txtTime.setText(textChatItem.getShortDate());
        txtContent.setText(textChatItem.getMessage());

        setUserAvatar(imgAvatar, baseChatItem);
    }
}