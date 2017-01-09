package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.TextChatItem;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BaseChatItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context, ArrayList<BaseChatItem> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat_text_item,parent, false);
        ViewHolderTextMessage viewHolder = new ViewHolderTextMessage(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseChatItem item = datas.get(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseChatItem baseChatItem = datas.get(position);
        if (baseChatItem instanceof TextChatItem) {

        }
    }

    public static class ViewHolderTextMessage extends RecyclerView.ViewHolder {

        public ViewHolderTextMessage(View root) {
            super(root);

        }
    }

    public static class ViewHolderTextMessageReply extends RecyclerView.ViewHolder {

        public ViewHolderTextMessageReply(View root) {
            super(root);

        }
    }

    public void clearData(){
        datas.clear();
        notifyDataSetChanged();
    }

    public void add(ChatItem item) {
        datas.add(item);
        notifyDataSetChanged();
    }

    public void clearAndAddNewData(ArrayList<BaseChatItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(ChatItem item, int position);
    }
}
