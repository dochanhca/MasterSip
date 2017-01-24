package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 1/24/17.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final int FIRST_POSITION = 0;
    private List<ImageItem> photos;
    private final Context context;
    private final int gender;
    private UserPhotoAdapter.OnItemClickListener onItemClickListener;

    public GalleryAdapter(Context context, List<ImageItem> photos, int gender) {
        this.photos = photos;
        this.context = context;
        this.gender = gender;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_photo, parent, false);
        final GalleryAdapter.ViewHolder viewHolder = new GalleryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageItem item = photos.get(position);
        updatePhotoView(item, holder, position);
    }

    private void updatePhotoView(ImageItem item,GalleryAdapter.ViewHolder holder,final int position) {
        int drawableId = gender == UserItem.MALE ? R.drawable.ic_boy_default :
                R.drawable.ic_girl_default;

        Glide.with(context).load(item.getOriginUrl())
                .error(drawableId).placeholder(drawableId)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.imgPhoto);

        if (item.getImageStatus() == ImageItem.IMAGE_APPROVED){
            holder.txtApproving.setVisibility(View.GONE);
            holder.imgMask.setVisibility(View.GONE);
            holder.imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onUserImageClick(position);
                }
            });
        }else {
            holder.txtApproving.setVisibility(View.VISIBLE);
            holder.imgMask.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<ImageItem> photos) {
        this.photos = photos;
    }

    public void addPhoto(ImageItem photo) {
        this.photos.add(FIRST_POSITION,photo);
    }

    public void setMorePhotos(List<ImageItem> photos) {
        this.photos.addAll(photos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;
        ImageView imgMask;
        TextView txtApproving;

        public ViewHolder(View root) {
            super(root);
            imgPhoto = (ImageView) root.findViewById(R.id.img_photo);
            imgMask = (ImageView) root.findViewById(R.id.img_mask_approving);
            txtApproving = (TextView) root.findViewById(R.id.txt_approving);
        }
    }



    public void setOnItemClickListener(UserPhotoAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onUserImageClick(int position);
    }
}
