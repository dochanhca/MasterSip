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
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by ducpv on 2/7/17.
 */

public class GalleryModeFourAdapter extends RecyclerView.Adapter<GalleryModeFourAdapter.ViewHolder> {

    private List<ImageItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public GalleryModeFourAdapter(Context context, List<ImageItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_gallery_mode_four, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageItem item = data.get(position);

        int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();

        Glide.with(context).load(item.getOriginUrl()).
                placeholder(defaultImageId).
                centerCrop().
                dontAnimate().
                dontTransform().
                error(defaultImageId).into(holder.imgPhoto);

        holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });

        if (item.getImageStatus() == ImageItem.IMAGE_APPROVED) {
            holder.txtApproving.setVisibility(View.GONE);
            holder.imgMaskApproving.setVisibility(View.GONE);
        } else {
            holder.txtApproving.setVisibility(View.VISIBLE);
            holder.imgMaskApproving.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(ImageItem item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<ImageItem> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPhoto;
        private ImageView imgMaskApproving;
        private TextView txtApproving;

        public ViewHolder(View root, Context context) {
            super(root);

            imgPhoto = (ImageView) root.findViewById(R.id.img_photo);
            imgMaskApproving = (ImageView) root.findViewById(R.id.img_mask_approving);
            txtApproving = (TextView) root.findViewById(R.id.txt_approving);

            setImageHeight(context);

        }

        private void setImageHeight(Context context) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgPhoto.getLayoutParams();
            layoutParams.height = (int) getAvatarHeightInModeFour(context);
            layoutParams.width = (int) getAvatarHeightInModeFour(context);

            imgPhoto.setLayoutParams(layoutParams);
            imgMaskApproving.setLayoutParams(layoutParams);
        }

        private static float getAvatarHeightInModeFour(Context c) {
            float screenWidth = (c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen.item_offset_mode_four) * 5);
            float height = screenWidth / 4;
            return height;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
