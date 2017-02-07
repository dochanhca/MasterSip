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

    private final ChatAdapter.OnFriendAvatarClickListener onFriendAvatarClickListener;

    public BaseChatReplyViewHolder(View root, Context context, ChatAdapter.OnFriendAvatarClickListener onFriendAvatarClickListener) {
        super(root, context);
        this.onFriendAvatarClickListener = onFriendAvatarClickListener;
    }

    public void setFriendAvatarClickListener(ImageView imgAvatar) {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFriendAvatarClickListener.onFriendProfileClick();
            }
        });
    }
}
