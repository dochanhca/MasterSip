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
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 1/25/17.
 */

public class ViewHolderImageMessage extends BaseChatViewHolder {
    private ImageView imgChat;
    private TextView txtTime;
    private TextView txtState;
    private ChatAdapter.OnItemClickListener onItemClickListener;

    public ViewHolderImageMessage(View root, Context context, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(root, context);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void initView(View root) {
        imgChat = (ImageView) root.findViewById(R.id.img_chat_item);
        txtTime = (TextView) root.findViewById(R.id.txt_time);
        txtState = (TextView) root.findViewById(R.id.txt_state);
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
        txtState.setVisibility(
                imageChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                        View.VISIBLE : View.GONE);
        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();
        ImageItem imageItem = Utils.calculateChatImageSize(getContext(), imageChatItem.getImageItem());
        Utils.setChatImageSize(imgChat, imageItem.getWidth(), imageItem.getHeight());

        Glide.with(getContext()).load(imageChatItem.getImageItem().getOriginUrl())
                .override(imageItem.getWidth(), imageItem.getHeight())
                .placeholder(defaultImageId)
                .error(defaultImageId)
                .thumbnail(0.1f)
                .centerCrop()
                .dontAnimate()
                .into(imgChat);
    }
}