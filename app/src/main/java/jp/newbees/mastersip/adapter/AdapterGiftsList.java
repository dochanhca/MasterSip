package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.GiftItem;

/**
 * Created by vietbq on 2/2/17.
 */

public class AdapterGiftsList extends RecyclerView.Adapter<AdapterGiftsList.GiftViewHolder> {
    private final Context context;
    private ArrayList<GiftItem> giftItems;

    public AdapterGiftsList(Context context, ArrayList<GiftItem> giftItems) {
        this.giftItems = giftItems;
        this.context = context;
    }

    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift, parent, false);
        final AdapterGiftsList.GiftViewHolder viewHolder = new AdapterGiftsList.GiftViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GiftViewHolder holder, int position) {
        GiftItem giftItem = this.giftItems.get(position);
        String price = String.valueOf(giftItem.getPrice())+"pt";
        String giftUrl = giftItem.getGiftImage().getOriginUrl();
        holder.txtGiftName.setText(giftItem.getName());
        holder.txtGiftPrice.setText(price);
        Glide.with(context).load(giftUrl).into(holder.imgGiftImage);
    }

    @Override
    public int getItemCount() {
        return giftItems.size();
    }

    public void addAll(List<GiftItem> giftItems) {
        this.giftItems.addAll(giftItems);
    }

    public class GiftViewHolder extends RecyclerView.ViewHolder{
        private TextView txtGiftName;
        private CircleImageView imgGiftImage;
        private TextView txtGiftPrice;

        public GiftViewHolder(View itemView, Context context) {
            super(itemView);
            txtGiftName = (TextView) itemView.findViewById(R.id.txt_gift_name);
            imgGiftImage = (CircleImageView) itemView.findViewById(R.id.img_gift);
            txtGiftPrice = (TextView) itemView.findViewById(R.id.txt_gift_price);
            setImageHeight(context);
        }

        private void setImageHeight(Context context) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imgGiftImage.getLayoutParams();
            float size = getGiftSize(context);
            layoutParams.height = (int) size;
            layoutParams.width = (int) size;
            imgGiftImage.setLayoutParams(layoutParams);
        }

        private float getGiftSize(Context c) {
            float size = ((c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen._10dp) * 9) )/3;
            return size;
        }
    }
}
