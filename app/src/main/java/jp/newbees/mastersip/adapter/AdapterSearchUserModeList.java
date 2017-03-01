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

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 12/27/16.
 */
public class AdapterSearchUserModeList extends RecyclerView.Adapter<AdapterSearchUserModeList.ViewHolder> {

    private List<UserItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterSearchUserModeList(Context context, List<UserItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_content_mode_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                onItemClickListener.onItemClick(data.get(position), position);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem item = data.get(position);
        Locale locale = Utils.getCurrentLocale(context);
        PrettyTime prettyTime = new PrettyTime(locale);

        String lastLogin = prettyTime.format(DateTimeUtils.convertStringToDate(item.getLastLogin(),
                DateTimeUtils.SERVER_DATE_FORMAT));

        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();

        Glide.with(context).load(item.getAvatarItem().getOriginUrl()).thumbnail(0.1f).
                placeholder(defaultImageId).
                error(defaultImageId).into(holder.imgAvatar);

        holder.txtTitle.setText(item.getUsername() + " " + Utils.getAge(item.getDateOfBirth())
                + context.getString(R.string.year_old));
        holder.txtValue.setText(item.getMemo());
        holder.txtTime.setText(lastLogin);
        holder.txtLocation.setText(item.getLocation().getTitle());

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
        ImageView imgAvatar;
        ImageView imgAvailableCall;
        ImageView imgAvailableVideo;
        TextView txtTime;
        TextView txtLocation;
        TextView txtTitle;
        TextView txtValue;
        RelativeLayout content;

        public ViewHolder(View root) {
            super(root);
            imgAvailableCall = (ImageView) root.findViewById(R.id.img_available_call);
            imgAvailableVideo = (ImageView) root.findViewById(R.id.img_available_video);
            imgAvatar = (ImageView) root.findViewById(R.id.img_avatar);
            txtTime = (TextView) root.findViewById(R.id.txt_time);
            txtLocation = (TextView) root.findViewById(R.id.txt_location);
            txtTitle = (TextView) root.findViewById(R.id.txt_title);
            txtValue = (TextView) root.findViewById(R.id.txt_value);
            content = (RelativeLayout) root.findViewById(R.id.content);
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
