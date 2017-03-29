package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.utils.ConfigManager;

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

    protected void setUserAvatar(ImageView imageView, BaseChatItem baseChatItem) {
        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();
        if (baseChatItem.getOwner().getAvatarItem() != null) {
            Glide.with(getContext()).load(baseChatItem.getOwner().getAvatarItem().getThumbUrl()).placeholder(defaultImageId).
                    error(defaultImageId).into(imageView);
        } else {
            imageView.setImageResource(defaultImageId);
        }
    }
}
