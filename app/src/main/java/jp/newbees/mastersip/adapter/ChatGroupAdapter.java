package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Iterator;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by ducpv on 2/2/17.
 */

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ViewHolder> {

    private List<RoomChatItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatGroupAdapter(Context context, List<RoomChatItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.content.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            onItemClickListener.onRoomChatItemClick(data.get(position), position);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RoomChatItem item = data.get(position);

        holder.bindView(item, context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    public void add(RoomChatItem item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<RoomChatItem> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<RoomChatItem> getData() {
        return this.data;
    }

    public void removeSelectedItem() {
        for (Iterator<RoomChatItem> item = data.iterator(); item.hasNext(); ) {
            RoomChatItem roomchatItem = item.next();
            if (roomchatItem.isSelected()) {
                item.remove();
            }
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtMessage;
        private TextView txtMessageNumber;
        private TextView txtTime;
        private ImageView imgAvatar;
        private RelativeLayout content;
        private CheckBox cbSelect;

        public ViewHolder(View root) {
            super(root);
            txtUserName = (TextView) root.findViewById(R.id.txt_user_name);
            txtMessage = (TextView) root.findViewById(R.id.txt_message);
            txtMessageNumber = (TextView) root.findViewById(R.id.txt_message_number);
            txtTime = (TextView) root.findViewById(R.id.txt_time);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            content = (RelativeLayout) root.findViewById(R.id.content);
            cbSelect = (CheckBox) root.findViewById(R.id.cb_select);
        }

        public void bindView(RoomChatItem item, Context context) {
            txtUserName.setText(item.getUserChat().getUsername());
            txtMessage.setText(item.getLastMessage());
            txtTime.setText(DateTimeUtils.getShortTime(item.getLastMessageTimeStamp()));

            if (item.getNumberMessageUnRead() > 0) {
                txtMessageNumber.setVisibility(View.VISIBLE);
                txtMessageNumber.setText(String.valueOf(item.getNumberMessageUnRead()));
            } else {
                txtMessageNumber.setVisibility(View.INVISIBLE);
            }

            int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();

            if (item.getUserChat().getAvatarItem() != null) {
                Glide.with(context).load(item.getUserChat().getAvatarItem().getOriginUrl()).
                        thumbnail(0.1f).
                        placeholder(defaultImageId).
                        error(defaultImageId).into(imgAvatar);
            }

            cbSelect.setVisibility(item.isShowingCheckbox() ? View.VISIBLE : View.GONE);
            cbSelect.setChecked(item.isSelected() ? true : false);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onRoomChatItemClick(RoomChatItem item, int position);
    }
}
