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

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 12/27/16.
 */
public class AdapterSearchUserModeTwo extends RecyclerView.Adapter<AdapterSearchUserModeTwo.ViewHolder> {

    private ArrayList<UserItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterSearchUserModeTwo(Context context, ArrayList<UserItem> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_user_content_mode_two, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserItem item = datas.get(position);
        holder.txtContent.setText(item.getUsername());
        holder.txtAbout.setText(item.getMemo());

        Glide.with(context).load(item.getAvatarItem().getOriginUrl()).
                placeholder(R.drawable.ic_boy_default).
                error(R.drawable.ic_boy_default).into(holder.imgAvatar);
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtContent;
        ImageView imgActionTop;
        TextView txtAbout;

        public ViewHolder(View root, Context context) {
            super(root);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            imgActionTop = (ImageView) root.findViewById(R.id.img_action_top);
            txtAbout = (TextView) root.findViewById(R.id.txt_about);
            txtContent = (TextView) root.findViewById(R.id.txt_content);

            setImageHeight(context);
        }

        private void setImageHeight(Context context) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgAvatar.getLayoutParams();
            layoutParams.height = (int) getAvatarHeightInModeTwo(context);
            imgAvatar.setLayoutParams(layoutParams);
        }

        public static float getAvatarHeightInModeTwo(Context c) {
            float screenWidth = (c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen.item_offset_mode_two) * 3);
            float height = screenWidth / 2;
            return height;
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
