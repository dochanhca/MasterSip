package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 12/27/16.
 */
public class AdapterSearUserModeList extends RecyclerView.Adapter<AdapterSearUserModeList.ViewHolder> {

    private ArrayList<UserItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterSearUserModeList(Context context, ArrayList<UserItem> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_content_mode_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem item = datas.get(position);
        Glide.with(context).load(item.getAvatarItem().getOriginUrl()).thumbnail(0.1f).
                placeholder(R.drawable.ic_boy_default).
                error(R.drawable.ic_boy_default).into(holder.imgAvatar);
        holder.txtTitle.setText(item.getUsername() + " " + Utils.getAge(item.getDateOfBirth()));
        holder.txtValue.setText(item.getMemo());
        holder.txtTime.setText(item.getAvailableTimeItem().getTitle());
        holder.txtLocation.setText(item.getLocation().getTitle());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgAvatar;
        ImageView imgActionTop;
        TextView txtTime;
        TextView txtLocation;
        TextView txtTitle;
        TextView txtValue;
        RelativeLayout content;

        public ViewHolder(View root) {
            super(root);
            imgActionTop = (ImageView) root.findViewById(R.id.img_action_top);
            imgAvatar = (RoundedImageView) root.findViewById(R.id.img_avatar);
            txtTime = (TextView) root.findViewById(R.id.txt_time);
            txtLocation = (TextView) root.findViewById(R.id.txt_location);
            txtTitle = (TextView) root.findViewById(R.id.txt_title);
            txtValue = (TextView) root.findViewById(R.id.txt_value);
            content = (RelativeLayout) root.findViewById(R.id.txt_content);
        }
    }

    public void clearData() {
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
