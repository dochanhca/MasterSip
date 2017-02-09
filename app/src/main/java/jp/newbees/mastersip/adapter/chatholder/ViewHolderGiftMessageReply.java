package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 2/3/17.
 */

public class ViewHolderGiftMessageReply extends BaseChatReplyViewHolder<GiftChatItem> {
    private CircleImageView imgGift;
    private TextView txtContent;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderGiftMessageReply(View root, Context context , ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
    }

    @Override
    protected void initView(View root) {
        imgGift = (CircleImageView) root.findViewById(R.id.img_gift);
        txtContent = (HiraginoTextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        setFriendAvatarClickListener(imgAvatar);
    }

    @Override
    public void bindView(GiftChatItem giftChatItem) {
        String imageUrl = giftChatItem.getGiftItem().getGiftImage().getOriginUrl();
        Glide.with(getContext()).load(imageUrl).into(imgGift);
        txtContent.setText(giftChatItem.getContent());
        txtTime.setText(giftChatItem.getShortDate());

        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();
        if (giftChatItem.getOwner().getAvatarItem() != null) {
            Glide.with(getContext()).load(giftChatItem.getOwner().getAvatarItem().getThumbUrl()).placeholder(defaultImageId).
                    error(defaultImageId).into(imgAvatar);
        } else {
            imgAvatar.setImageResource(defaultImageId);
        }
    }
}
