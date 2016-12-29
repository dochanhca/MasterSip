package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 12/23/16.
 */

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private ArrayList<UserItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public SearchUserAdapter(Context context, ArrayList<UserItem> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_mode_four,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem item = datas.get(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View root) {
            super(root);
            
        }
    }

    public void clearData(){
        datas.clear();
        notifyDataSetChanged();
    }

    public void add(UserItem item) {
        datas.add(item);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<UserItem> datas) {
        datas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(UserItem item, int position);
    }
}
