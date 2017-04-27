package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GiftChatItem;

/**
 * Created by vietbq on 2/3/17.
 */

public class ViewHolderGiftMessageReply extends BaseChatReplyViewHolder<GiftChatItem> {
    private ImageView imgGift;
    private TextView txtContent;
    private TextView txtTime;
    private ImageView imgAvatar;

    public ViewHolderGiftMessageReply(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context, onItemClickListener);
    }

    @Override
    protected void initView(View root) {
        imgGift = (ImageView) root.findViewById(R.id.img_gift);
        txtContent = (HiraginoTextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        setFriendAvatarClickListener(imgAvatar);
    }

    @Override
    public void bindView(GiftChatItem giftChatItem) {
        String imageUrl = giftChatItem.getGiftItem().getGiftImage().getOriginUrl();
        loadGiftImage(imageUrl, imgGift);

        txtContent.setText(giftChatItem.getContent());
        txtTime.setText(giftChatItem.getShortDate());

        setUserAvatar(imgAvatar, giftChatItem);
    }
}
