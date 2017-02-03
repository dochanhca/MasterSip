package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderImageMessageReply extends BaseChatViewHolder {
    private ImageView imgChat;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderImageMessageReply(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        imgChat = (ImageView) root.findViewById(R.id.img_chat_item);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
    }

    @Override
    public void bindView(BaseChatItem item) {
        ImageChatItem imageChatItem = (ImageChatItem) item;
        txtTime.setText(imageChatItem.getShortDate());

        int defaultChatImage = ConfigManager.getInstance().getImageCalleeDefault();
        Glide.with(getContext()).load(imageChatItem.getImageItem().getThumbUrl())
                .placeholder(defaultChatImage)
                .error(defaultChatImage).into(imgChat);

        int defaultAvatar = ConfigManager.getInstance().getImageCalleeDefault();
        if (imageChatItem.getOwner().getAvatarItem() != null) {
            Glide.with(getContext()).load(imageChatItem.getOwner().getAvatarItem().getThumbUrl())
                    .placeholder(defaultAvatar)
                    .error(defaultAvatar).into(imgAvatar);
        } else {
            imgAvatar.setImageResource(defaultAvatar);
        }
    }

}