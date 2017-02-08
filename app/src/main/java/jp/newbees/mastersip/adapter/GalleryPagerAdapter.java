package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.ImageItem;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ducpv on 2/7/17.
 */

public class GalleryPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ImageItem> photos;

    public GalleryPagerAdapter(List<ImageItem> photos, Context context) {
        this.photos = photos;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = inflater.inflate(R.layout.item_gallery, container, false);

        ImageItem imageItem = photos.get(position);

        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.img_photo);
        ImageView imgApproving = (ImageView) convertView.findViewById(R.id.img_mask_approving);

        TextView txtApproving = (TextView) convertView.findViewById(R.id.txt_approving);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgPhoto, true);

        Glide.with(context).load(imageItem.getOriginUrl()).fitCenter()
                .into(imgPhoto);
        if (imageItem.getImageStatus() == ImageItem.IMAGE_APPROVED) {
            txtApproving.setVisibility(View.GONE);
            imgApproving.setVisibility(View.GONE);
        } else {
            txtApproving.setVisibility(View.VISIBLE);
            imgApproving.setVisibility(View.VISIBLE);
        }

        container.addView(convertView, 0);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
