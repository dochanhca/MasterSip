package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by ducpv on 1/19/17.
 */


public class UserPhotoAdapter extends RecyclerView.Adapter<UserPhotoAdapter.ViewHolder> {

    private int gender;
    private List<ImageItem> datas;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public UserPhotoAdapter(Context context, List<ImageItem> datas, int gender) {
        this.datas = datas;
        this.context = context;
        this.gender = gender;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_photo, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                onItemClickListener.onUserImageClick(position);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageItem item = datas.get(position);

        loadUserProfileImage(item, holder);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;

        public ViewHolder(View root) {
            super(root);

            imgUser = (ImageView) root.findViewById(R.id.img_user);
        }
    }

    private void loadUserProfileImage(ImageItem item, ViewHolder holder) {
        int drawableId = gender == UserItem.MALE ? R.drawable.ic_boy_default :
                R.drawable.ic_girl_default;

        Glide.with(context).load(item.getOriginUrl())
                .error(drawableId).placeholder(drawableId)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.imgUser);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onUserImageClick(int position);
    }
}