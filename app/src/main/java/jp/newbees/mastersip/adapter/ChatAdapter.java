package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.chatholder.BaseChatViewHolder;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderCallMessage;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderCallMessageReply;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderGiftMessage;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderGiftMessageReply;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderHeader;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderImageMessage;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderImageMessageReply;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderTextMessage;
import jp.newbees.mastersip.adapter.chatholder.ViewHolderTextMessageReply;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OFFSET_RETURN_TYPE = 100;
    private Date initDay;

    private List<BaseChatItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public ChatAdapter(Context context, List<BaseChatItem> data) {
        this.initDay = DateTimeUtils.getDateWithoutTime(Calendar.getInstance().getTime());
        this.data = data;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        switch (viewType) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                if (isReplyMessage) {
                    view = layoutInflater.inflate(R.layout.reply_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessageReply(view, context, onItemClickListener);
                } else {
                    view = layoutInflater.inflate(R.layout.my_chat_text_item, parent, false);
                    viewHolder = new ViewHolderTextMessage(view, context);
                }
                break;
            case BaseChatItem.ChatType.CHAT_IMAGE:
                if (isReplyMessage) {
                    view = layoutInflater.inflate(R.layout.reply_chat_image_item, parent, false);
                    viewHolder = new ViewHolderImageMessageReply(view, context, onItemClickListener);
                } else {
                    view = layoutInflater.inflate(R.layout.my_chat_image_item, parent, false);
                    viewHolder = new ViewHolderImageMessage(view, context, onItemClickListener);
                }
                break;
            case BaseChatItem.ChatType.HEADER:
                view = layoutInflater.inflate(R.layout.header_chat_recycle_view, parent, false);
                viewHolder = new ViewHolderHeader(view, context);
                break;
            case BaseChatItem.ChatType.CHAT_GIFT:
                if (isReplyMessage) {
                    view = layoutInflater.inflate(R.layout.reply_chat_gift_item, parent, false);
                    viewHolder = new ViewHolderGiftMessageReply(view, context, onItemClickListener);
                } else {
                    view = layoutInflater.inflate(R.layout.my_chat_gift_item, parent, false);
                    viewHolder = new ViewHolderGiftMessage(view, context);
                }
                break;
            case BaseChatItem.ChatType.CHAT_VOICE_CALL:
            case BaseChatItem.ChatType.CHAT_VIDEO_CALL:
            case BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL:
                if (isReplyMessage) {
                    view = layoutInflater.inflate(R.layout.reply_chat_call_item, parent, false);
                    viewHolder = new ViewHolderCallMessageReply(view, context, onItemClickListener);
                } else {
                    view = layoutInflater.inflate(R.layout.my_chat_call_item, parent, false);
                    viewHolder = new ViewHolderCallMessage(view, context);
                }
                break;
            default:
                view = layoutInflater.inflate(R.layout.header_chat_recycle_view, parent, false);
                viewHolder = new ViewHolderHeader(view, context);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseChatItem item = data.get(position);
        ((BaseChatViewHolder) holder).bindView(item);
        setLayoutParam(holder, item);
    }

    private void setLayoutParam(RecyclerView.ViewHolder holder, BaseChatItem item) {
        int viewType = holder.getItemViewType();
        boolean isReplyMessage = viewType > OFFSET_RETURN_TYPE;
        if (isReplyMessage) {
            viewType -= OFFSET_RETURN_TYPE;
        }

        View itemView = holder.itemView;

        final GridSLM.LayoutParams layoutParams = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        if (viewType == BaseChatItem.ChatType.HEADER) {
            layoutParams.headerDisplay = LayoutManager.LayoutParams.HEADER_OVERLAY | LayoutManager.LayoutParams.HEADER_STICKY;
            layoutParams.headerEndMarginIsAuto = true;
            layoutParams.headerStartMarginIsAuto = true;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        layoutParams.setSlm(LinearSLM.ID);
        layoutParams.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseChatItem baseChatItem = data.get(position);
        int type = baseChatItem.getChatType();
        if (baseChatItem.getChatType() != BaseChatItem.ChatType.HEADER && !baseChatItem.isOwner()) {
            type += OFFSET_RETURN_TYPE;
        }
        return type;
    }

    public int getLastMessageID() {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getChatType() != BaseChatItem.ChatType.HEADER) {
                return data.get(i).getMessageId();
            }
        }
        return 0;
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    public List<BaseChatItem> getData() {
        return data;
    }

    public void add(BaseChatItem item) {
        data.add(item);
    }

    public void addItemAndHeaderIfNeed(BaseChatItem item) {
        if (hasNewDay(item)) {
            updateAllHeaderItem(false);
        }

        if (needToAddHeader(item)) {
            addHeaderItem(item);
        }
        int sectionFirstPosition = getLastHeader();
        item.setSectionFirstPosition(sectionFirstPosition);
        add(item);
        notifyDataSetChanged();
    }

    private void updateAllHeaderItem(boolean needNotify) {
        for (int i = 0; i < data.size(); i++) {
            BaseChatItem item = data.get(i);
            Date date = DateTimeUtils.convertStringToDate(item.getFullDate(), DateTimeUtils.ENGLISH_DATE_FORMAT);
            if (item.getChatType() == BaseChatItem.ChatType.HEADER) {
                item.setDisplayDate(DateTimeUtils.getHeaderDisplayDateInChatHistory(date, context));
                if (needNotify) notifyItemChanged(i);
            }
        }
    }

    private void addHeaderItem(BaseChatItem item) {
        BaseChatItem header = getHeaderChatItem(
                DateTimeUtils.convertStringToDate(item.getFullDate(), DateTimeUtils.ENGLISH_DATE_FORMAT),
                getItemCount());
        header.setOwner(item.getOwner());
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
            Logger.e("ChatAdapter", e.getMessage());
        }
        return false;
    }

    private int getLastHeader() {
        for (int i = data.size() - 1; i >= 0; i--) {
            if (data.get(i).getChatType() == BaseChatItem.ChatType.HEADER) {
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
            Date lastDate = DateTimeUtils.convertStringToDate(data.get(getItemCount() - 1).getFullDate(),
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
        header.setDisplayDate(DateTimeUtils.getHeaderDisplayDateInChatHistory(date, context));
        header.setSectionFirstPosition(sectionFirstPosition);
        return header;
    }

    public void clearAndAddNewData(List<BaseChatItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addDataFromBeginning(List<BaseChatItem> data) {
        List<BaseChatItem> newData = new ArrayList<>();

        newData.addAll(data);
        newData.addAll(this.data);
        newData = updateSelectionFirstPosition(newData);
        clearAndAddNewData(newData);
    }

    /**
     * When load more items, new items added from first position
     * So old header position was changed -> need update old item's header position
     *
     * @param newData
     * @return
     */
    private List<BaseChatItem> updateSelectionFirstPosition(List<BaseChatItem> newData) {
        int headerPosition = 0;
        for (int i = 1; i < newData.size(); i++) {
            BaseChatItem item = newData.get(i);
            if (item.getChatType() == BaseChatItem.ChatType.HEADER) {
                item.setSectionFirstPosition(i);
                headerPosition = i;
            } else {
                item.setSectionFirstPosition(headerPosition);
            }
        }
        return newData;
    }

    public void removeHeaderItemIfDuplicated(List<BaseChatItem> data) {
        BaseChatItem currentHeader = this.data.get(0);
        for (BaseChatItem item : data) {
            if (item.getChatType() == BaseChatItem.ChatType.HEADER &&
                    item.getDisplayDate().equals(currentHeader.getDisplayDate())) {
                this.data.remove(0);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onImageClick(int position);

        void onFriendAvatarClick();
    }

    public BaseChatItem getLastSendeeUnreadMessage() {
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = data.get(i);
            if (baseChatItem.getChatType() != BaseChatItem.ChatType.HEADER &&
                    baseChatItem.getChatType() != BaseChatItem.ChatType.CHAT_VOICE_CALL &&
                    baseChatItem.getChatType() != BaseChatItem.ChatType.CHAT_VIDEO_CALL &&
                    baseChatItem.getChatType() != BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL &&
                    !baseChatItem.isOwner() && baseChatItem.getMessageState() != BaseChatItem.MessageState.STT_READ) {
                return baseChatItem;
            }
        }
        return null;
    }

    public void updateSendeeLastMessageStateToRead() {
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = data.get(i);
            if (baseChatItem.getChatType() != BaseChatItem.ChatType.HEADER &&
                    !baseChatItem.isOwner()) {
                baseChatItem.setMessageState(BaseChatItem.MessageState.STT_READ);
                return;
            }
        }
    }

    public void updateOwnerStateMessageToRead(BaseChatItem readChatItem) {
        boolean hasReadChatItem = false;
        for (int i = getItemCount() - 1; i >= 0; i--) {
            BaseChatItem baseChatItem = data.get(i);
            if (hasReadChatItem) {
                if (baseChatItem.getChatType() != BaseChatItem.ChatType.HEADER && baseChatItem.isOwner()) {
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

}
