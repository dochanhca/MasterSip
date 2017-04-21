package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by ducpv on 4/21/17.
 */

public class OnlineListAdapter extends RecyclerView.Adapter<OnlineListAdapter.ViewHolder> {

    private List<UserItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public OnlineListAdapter(Context context, List<UserItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_list,parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.btnChangeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                onItemClickListener.onChangeOnlineSettingClick(position);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem item = data.get(position);
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

    public void add(UserItem item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<UserItem> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<UserItem> getData() {
        return this.data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAvatar;
        private TextView txtUserName;
        private Button btnChangeSetting;

        public ViewHolder(View root) {
            super(root);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            txtUserName = (TextView) root.findViewById(R.id.txt_user_name);
            btnChangeSetting = (Button) root.findViewById(R.id.btn_change_setting);
        }

        public void bindView(UserItem userItem, Context context) {
            int deafaultAvatar = ConfigManager.getInstance().getImageCalleeDefault();
            Glide.with(context).load(userItem.getAvatarItem().getOriginUrl())
                    .placeholder(deafaultAvatar).error(deafaultAvatar)
                    .centerCrop().thumbnail(0.1f)
                    .into(imgAvatar);
            txtUserName.setText(userItem.getUsername());
        }
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public interface OnItemClickListener {
        void onChangeOnlineSettingClick(int position);
    }
}
