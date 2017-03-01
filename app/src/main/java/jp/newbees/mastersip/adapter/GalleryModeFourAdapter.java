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

public class GalleryModeFourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    private List<ImageItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public GalleryModeFourAdapter(Context context, List<ImageItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_gallery_mode_four, parent, false);
            final ItemViewHolder holder = new ItemViewHolder(view, context);

            holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    onItemClickListener.onItemClick(position);
                }
            });
            return holder;

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_photo_gallery, parent, false);
            return new FooterViewHolder(view, context);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ImageItem item = data.get(position);

            ((ItemViewHolder) holder).bindView(item);
        } else if (holder instanceof FooterViewHolder){
            ((FooterViewHolder) holder).bindView(data.size());
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionFooter(int position) {
        return position == data.size();
    }

    public void add(ImageItem item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<ImageItem> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNumberPhoto;
        private Context context;

        public FooterViewHolder(View root, Context context) {
            super(root);
            this.context = context;
            txtNumberPhoto = (TextView) itemView.findViewById(R.id.txt_number_photo);
        }

        public void bindView(int size) {
            StringBuilder numberPhoto = new StringBuilder();
            numberPhoto.append(size).append(context.getString(R.string.photos));
            txtNumberPhoto.setText(numberPhoto.toString());
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPhoto;
        private ImageView imgMaskApproving;
        private TextView txtApproving;
        private Context context;

        public ItemViewHolder(View root, Context context) {
            super(root);
            this.context = context;
            imgPhoto = (ImageView) root.findViewById(R.id.img_photo);
            imgMaskApproving = (ImageView) root.findViewById(R.id.img_mask_approving);
            txtApproving = (TextView) root.findViewById(R.id.txt_approving);

            setImageHeight(context);
        }

        public void bindView(ImageItem imageItem) {
            int defaultImageId = ConfigManager.getInstance().getImageCalleeDefault();

            Glide.with(context).load(imageItem.getOriginUrl()).
                    placeholder(defaultImageId).
                    centerCrop().
                    dontAnimate().
                    dontTransform().
                    error(defaultImageId).into(imgPhoto);

            if (imageItem.getImageStatus() == ImageItem.IMAGE_APPROVED) {
                txtApproving.setVisibility(View.GONE);
                imgMaskApproving.setVisibility(View.GONE);
            } else {
                txtApproving.setVisibility(View.VISIBLE);
                imgMaskApproving.setVisibility(View.VISIBLE);
            }
        }

        private void setImageHeight(Context context) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgPhoto.getLayoutParams();
            layoutParams.height = (int) getAvatarHeightInModeFour(context);
            layoutParams.width = (int) getAvatarHeightInModeFour(context);

            imgPhoto.setLayoutParams(layoutParams);
            imgMaskApproving.setLayoutParams(layoutParams);
        }

        private static float getAvatarHeightInModeFour(Context c) {
            float screenWidth = c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen.item_offset_mode_four) * 5;
            return screenWidth / 4;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
