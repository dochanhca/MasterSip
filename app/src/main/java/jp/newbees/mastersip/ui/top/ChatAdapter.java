package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OFFSET_RETURN_TYPE = 100;

    private List<BaseChatItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context, List<BaseChatItem> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        switch (viewType) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                if (isReplyMessage) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessageReply(view);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessage(view);
                }
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseChatItem item = datas.get(position);

        int viewType = holder.getItemViewType();
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        switch (viewType) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                TextChatItem textChatItem = (TextChatItem) item;
                if (isReplyMessage) {
                    ViewHolderTextMessageReply viewHolderTextMessageReply = (ViewHolderTextMessageReply) holder;
                    viewHolderTextMessageReply.txtTime.setText(textChatItem.getShortDate());
                    viewHolderTextMessageReply.txtContent.setText(textChatItem.getMessage());

                    int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();
                    if (item.getOwner().getAvatarItem() != null) {
                        Glide.with(context).load(item.getOwner().getAvatarItem().getThumbUrl()).placeholder(defaultImageId).
                                error(defaultImageId).into(viewHolderTextMessageReply.imgAvatar);
                    } else {
                        viewHolderTextMessageReply.imgAvatar.setImageResource(defaultImageId);
                    }

                } else {
                    ViewHolderTextMessage viewHolderTextMessage = (ViewHolderTextMessage) holder;
                    viewHolderTextMessage.txtTime.setText(textChatItem.getShortDate());
                    viewHolderTextMessage.txtContent.setText(textChatItem.getMessage());
                    viewHolderTextMessage.txtState.setVisibility(
                            textChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ ?
                                    View.VISIBLE : View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseChatItem baseChatItem = datas.get(position);
        int type = baseChatItem.getChatType();
        if (!baseChatItem.isOwner()) {
            type += OFFSET_RETURN_TYPE;
        }
        return type;
    }

    public static class ViewHolderTextMessage extends RecyclerView.ViewHolder {
        private TextView txtContent;
        private TextView txtTime;
        private TextView txtState;

        public ViewHolderTextMessage(View root) {
            super(root);
            txtContent = (TextView) root.findViewById(R.id.txt_content);
            txtTime = (TextView) root.findViewById(R.id.txt_time);
            txtState = (TextView) root.findViewById(R.id.txt_state);
        }
    }

    public static class ViewHolderTextMessageReply extends RecyclerView.ViewHolder {
        private TextView txtContent;
        private TextView txtTime;
        private ImageView imgAvatar;

        public ViewHolderTextMessageReply(View root) {
            super(root);
            txtContent = (TextView) root.findViewById(R.id.txt_content);
            txtTime = (TextView) root.findViewById(R.id.txt_time);
            imgAvatar = (ImageView) root.findViewById(R.id.img_reply_avatar);
        }
    }

    public void clearData() {
        datas.clear();
        notifyDataSetChanged();
    }

    public void add(BaseChatItem item) {
        datas.add(item);
        notifyDataSetChanged();
    }

    public void clearAndAddNewData(List<BaseChatItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(BaseChatItem item, int position);
    }

    public BaseChatItem getLastSendeeUnreadMessage() {
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = datas.get(i);
            if (!baseChatItem.isOwner() && baseChatItem.getMessageState() != BaseChatItem.MessageState.STT_READ) {
                return baseChatItem;
            }
        }
        return null;
    }

    public void updateSendeeLastMessageStateToRead() {
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = datas.get(i);
            if (!baseChatItem.isOwner() && baseChatItem.getMessageState() != BaseChatItem.MessageState.STT_READ) {
                baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
                return;
            }
        }
    }

    public void updateOwnerStateMessageToRead(BaseChatItem readChatItem) {
        boolean hasReadChatItem = false;
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = datas.get(i);
            if (baseChatItem.getMessageId() == readChatItem.getMessageId()) {
                hasReadChatItem = true;
                baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
            }
            if (hasReadChatItem && baseChatItem.isOwner()) {
                if (baseChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ) {
                    notifyDataSetChanged();
                    return;
                } else {
                    baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
                }
            }
        }
        notifyDataSetChanged();
    }
}
