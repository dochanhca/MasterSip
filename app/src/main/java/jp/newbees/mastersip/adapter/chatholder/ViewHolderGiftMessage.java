package jp.newbees.mastersip.adapter.chatholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.GiftChatItem;

/**
 * Created by vietbq on 2/3/17.
 */

public class ViewHolderGiftMessage extends BaseChatViewHolder<GiftChatItem>{
    private ImageView imgGift;
    private HiraginoTextView txtContent;
    private TextView txtTime;
    private TextView txtState;

    public ViewHolderGiftMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        imgGift = (ImageView) root.findViewById(R.id.img_gift);
        txtContent = (HiraginoTextView) root.findViewById(R.id.txt_content);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtState = (TextView) root.findViewById(R.id.txt_state);
    }

    @Override
    public void bindView(GiftChatItem giftChatItem) {
        String imageUrl = giftChatItem.getGiftItem().getGiftImage().getOriginUrl();
        loadGiftImage(imageUrl, imgGift);

        txtContent.setText(giftChatItem.getContent());
        txtTime.setText(giftChatItem.getShortDate());
        txtState.setVisibility(
                giftChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                        View.VISIBLE : View.GONE);
    }

}
