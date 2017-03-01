package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 12/27/16.
 */
public class AdapterSearchUserModeTwo extends RecyclerView.Adapter<AdapterSearchUserModeTwo.ViewHolder> {

    private List<UserItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterSearchUserModeTwo(Context context, List<UserItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_user_content_mode_two, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view, context);

        viewHolder.viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                onItemClickListener.onItemClick(data.get(position), position);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserItem item = data.get(position);
        holder.txtName.setText(item.getUsername());
        holder.txtAbout.setText(item.getMemo());

        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();

        Glide.with(context).load(item.getAvatarItem().getOriginUrl()).
                placeholder(defaultImageId).
                error(defaultImageId).into(holder.imgAvatar);

        holder.imgAvailableCall.setVisibility(item.getSettings().getVoiceCall() == SettingItem.ON
                ? View.VISIBLE : View.GONE);
        holder.imgAvailableVideo.setVisibility(item.getSettings().getVideoCall() == SettingItem.ON
                ? View.VISIBLE : View.GONE);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private ImageView imgAvailableCall;
        private ImageView imgAvailableVideo;
        private TextView txtName;
        private TextView txtAbout;
        private ViewGroup viewGroup;

        public ViewHolder(View root, Context context) {
            super(root);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            imgAvailableCall = (ImageView) root.findViewById(R.id.img_available_call);
            imgAvailableVideo = (ImageView) root.findViewById(R.id.img_available_video);
            txtAbout = (TextView) root.findViewById(R.id.txt_about);
            txtName = (TextView) root.findViewById(R.id.txt_name);
            viewGroup = (ViewGroup) root.findViewById(R.id.view_group);

            setImageHeight(context);
        }

        private void setImageHeight(Context context) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgAvatar.getLayoutParams();
            layoutParams.height = (int) getAvatarHeightInModeTwo(context);
            imgAvatar.setLayoutParams(layoutParams);
        }

        public static float getAvatarHeightInModeTwo(Context c) {
            float screenWidth = c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen.item_offset_mode_two) * 3;
            return screenWidth / 2;
        }
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
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(UserItem item, int position);
    }
}
