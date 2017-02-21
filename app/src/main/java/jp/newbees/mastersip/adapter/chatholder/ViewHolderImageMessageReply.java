package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderImageMessageReply extends BaseChatReplyViewHolder {
    private ImageView imgChat;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderImageMessageReply(View root, Context context,
                                       ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
    }

    @Override
    protected void initView(View root) {
        imgChat = (ImageView) root.findViewById(R.id.img_chat_item);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        setFriendAvatarClickListener(imgAvatar);
        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onImageClick(getAdapterPosition());
            }
        });
    }

    @Override
    public void bindView(BaseChatItem item) {
        ImageChatItem imageChatItem = (ImageChatItem) item;
        txtTime.setText(imageChatItem.getShortDate());

        int w = Utils.getScreenWidth(getContext());
        int h = Utils.getScreenHeight(getContext());
        int defaultChatImage = ConfigManager.getInstance().getImageCalleeDefault();
        Glide.with(getContext()).load(imageChatItem.getImageItem().getThumbUrl())
                .override(w / 2, h / 2)
                .placeholder(defaultChatImage)
                .error(defaultChatImage)
                .fitCenter()
                .thumbnail(0.1f)
                .dontAnimate()
                .into(imgChat);

        int defaultAvatar = ConfigManager.getInstance().getImageCalleeDefault();
        if (imageChatItem.getOwner().getAvatarItem() != null) {
            Glide.with(getContext()).load(imageChatItem.getOwner().getAvatarItem().getThumbUrl())
                    .placeholder(defaultAvatar)
                    .error(defaultAvatar)
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(defaultAvatar);
        }
    }

}