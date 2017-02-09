package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;

/**
 * Created by thangit14 on 2/6/17.
 */

public abstract class BaseChatReplyViewHolder<T extends BaseChatItem> extends BaseChatViewHolder<T> {

    protected final ChatAdapter.OnItemClickListener onItemClickListener;

    public BaseChatReplyViewHolder(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context);
        this.onItemClickListener = onItemClickListener;
    }

    public void setFriendAvatarClickListener(ImageView imgAvatar) {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onFriendAvatarClick();
            }
        });
    }
}
