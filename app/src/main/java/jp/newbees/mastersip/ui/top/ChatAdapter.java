package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OFFSET_RETURN_TYPE = 100;
    private Date initDay;

    private List<BaseChatItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context, List<BaseChatItem> datas) {
        this.initDay = DateTimeUtils.getDateWithoutTime(Calendar.getInstance().getTime());
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                if (isReplyMessage) {
                    view = layoutInflater.inflate(R.layout.reply_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessageReply(view);
                } else {
                    view = layoutInflater.inflate(R.layout.my_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessage(view);
                }
                break;
            case BaseChatItem.ChatType.HEADER:
                view = layoutInflater.inflate(R.layout.header_chat_recycle_view, parent, false);
                viewHolder = new ViewHolderHeader(view);
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseChatItem item = datas.get(position);
        View itemView = holder.itemView;

        int viewType = holder.getItemViewType();
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        final GridSLM.LayoutParams layoutParams = GridSLM.LayoutParams.from(itemView.getLayoutParams());

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
            case BaseChatItem.ChatType.HEADER:
                layoutParams.headerDisplay = LayoutManager.LayoutParams.HEADER_OVERLAY | LayoutManager.LayoutParams.HEADER_STICKY;
                layoutParams.headerEndMarginIsAuto = true;
                layoutParams.headerStartMarginIsAuto = true;
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
                viewHolderHeader.txtContent.setText(item.getDisplayDate());
                break;
            default:

                break;
        }
        layoutParams.setSlm(LinearSLM.ID);
        layoutParams.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(layoutParams);
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

    public int getLastMessageID() {
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getChatType() != BaseChatItem.ChatType.HEADER) {
                return datas.get(i).getMessageId();
            }
        }
        return 0;
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

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        private TextView txtContent;

        public ViewHolderHeader(View root) {
            super(root);
            txtContent = (TextView) root.findViewById(R.id.txt_content);
        }
    }

    public void clearData() {
        datas.clear();
        notifyDataSetChanged();
    }

    private void add(BaseChatItem item) {
        datas.add(item);
    }

    public void addItemAndHeaderIfNeed(BaseChatItem item) {
        int sectionFirstPosition = 0;
        if (hasNewDay(item)) {
            updateAllHeaderItem(false);
        }

        if (needToAddHeader(item)) {
            addHeaderItem(item);
        } else {
            sectionFirstPosition = getLastHeader();
        }
        item.setSectionFirstPosition(sectionFirstPosition);
        add(item);
        notifyDataSetChanged();
    }

    private void updateAllHeaderItem(boolean needNotify) {
        for (int i = 0; i < datas.size(); i++) {
            BaseChatItem item = datas.get(i);
            Date date = DateTimeUtils.convertStringToDate(item.getFullDate(), DateTimeUtils.ENGLISH_DATE_FORMAT);
            if (item.getChatType() == BaseChatItem.ChatType.HEADER) {
                item.setFullDate(DateTimeUtils.getHeaderDateInChatHistory(date, context));
                if (needNotify) notifyItemChanged(i);
            }
        }
    }

    private void addHeaderItem(BaseChatItem item) {
        BaseChatItem header = getHeaderChatItem(
                DateTimeUtils.convertStringToDate(item.getFullDate(), DateTimeUtils.ENGLISH_DATE_FORMAT),
                getItemCount());
        add(header);
    }

    private boolean hasNewDay(BaseChatItem item) {
        try {
            Date date = DateTimeUtils.ENGLISH_DATE_FORMAT.parse(item.getFullDate());
            if (initDay.before(date)) {
                this.initDay = date;
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getLastHeader() {
        for (int i = datas.size() - 1; i >= 0; i--) {
            if (datas.get(i).getChatType() == BaseChatItem.ChatType.HEADER) {
                return i;
            }
        }
        return 0;
    }

    private boolean needToAddHeader(BaseChatItem item) {
        if (getItemCount() == 0) {
            return true;
        } else {
            Date newDate = DateTimeUtils.convertStringToDate(item.getFullDate(), DateTimeUtils.ENGLISH_DATE_FORMAT);
            Date lastDate = DateTimeUtils.convertStringToDate(datas.get(getItemCount() - 1).getFullDate(),
                    DateTimeUtils.ENGLISH_DATE_FORMAT);

            if (newDate.after(lastDate)) {
                return true;
            }
        }
        return false;
    }

    private BaseChatItem getHeaderChatItem(Date date, int sectionFirstPosition) {
        BaseChatItem header = new BaseChatItem();
        header.setChatType(BaseChatItem.ChatType.HEADER);
        header.setFullDate(DateTimeUtils.getHeaderDateInChatHistory(date, context));
        header.setSectionFirstPosition(sectionFirstPosition);
        return header;
    }

    public void clearAndAddNewData(List<BaseChatItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDataFromBeginning(List<BaseChatItem> datas) {
        ArrayList<BaseChatItem> newData = new ArrayList<>();
        newData.addAll(datas);
        newData.addAll(this.datas);
        clearAndAddNewData(newData);
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
            if (hasReadChatItem) {
                if (baseChatItem.isOwner()) {
                    if (baseChatItem.getMessageState() == BaseChatItem.MessageState.STT_READ) {
                        notifyDataSetChanged();
                        return;
                    } else {
                        baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
                    }
                }
            } else if (baseChatItem.getMessageId() == readChatItem.getMessageId()) {
                hasReadChatItem = true;
                baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
            }

        }
        notifyDataSetChanged();
    }


    private void notifyHeaderChanges() {
        for (int i = 0; i < datas.size(); i++) {
            BaseChatItem item = datas.get(i);
            if (item.getChatType() == BaseChatItem.ChatType.HEADER) {
                notifyItemChanged(i);
            }
        }
    }

}
