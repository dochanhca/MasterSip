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

public class ViewHolderImageMessage extends BaseChatViewHolder {
    private ImageView imgChat;
    private TextView txtTime;
    private TextView txtState;

    public ViewHolderImageMessage(View root, Context context) {
        super(root, context);
    }

    @Override
    protected void initView(View root) {
        imgChat = (ImageView) root.findViewById(R.id.img_chat_item);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtState = (TextView) root.findViewById(R.id.txt_state);
    }

    @Override
    public void bindView(BaseChatItem item) {
        ImageChatItem imageChatItem = (ImageChatItem) item;
        txtTime.setText(imageChatItem.getShortDate());
        txtState.setVisibility(
                imageChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                        View.VISIBLE : View.GONE);

        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();
        Glide.with(getContext()).load(imageChatItem.getImageItem().getThumbUrl())
                .placeholder(defaultImageId)
                .error(defaultImageId).into(imgChat);
    }

}