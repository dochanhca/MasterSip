package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.GiftItem;

/**
 * Created by vietbq on 2/2/17.
 */

public class AdapterGiftsList extends RecyclerView.Adapter<AdapterGiftsList.GiftViewHolder> {
    private final Context context;
    private List<GiftItem> giftItems;

    private OnGiftItemSelectListener giftItemSelectListener;

    public AdapterGiftsList(Context context, List<GiftItem> giftItems) {
        this.giftItems = giftItems;
        this.context = context;
    }

    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift, parent, false);
        return new GiftViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(GiftViewHolder holder, int position) {
        GiftItem giftItem = this.giftItems.get(position);
        String price = String.valueOf(giftItem.getPrice()) + context.getString(R.string.pt);
        String giftUrl = giftItem.getGiftImage().getOriginUrl();
        holder.txtGiftName.setText(giftItem.getName());
        holder.txtGiftPrice.setText(price);
        Glide.with(context).load(giftUrl).into(holder.imgGiftImage);
        bindSelectGiftAction(holder, giftItem);
    }

    private void bindSelectGiftAction(GiftViewHolder holder, GiftItem giftItem) {
        holder.llGift.setTag(giftItem);
        holder.llGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != giftItemSelectListener){
                    GiftItem giftItem = (GiftItem) view.getTag();
                    giftItemSelectListener.onGiftItemSelect(giftItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return giftItems.size();
    }

    public void addAll(List<GiftItem> giftItems) {
        this.giftItems.addAll(giftItems);
    }

    static class GiftViewHolder extends RecyclerView.ViewHolder {
        private TextView txtGiftName;
        private CircleImageView imgGiftImage;
        private TextView txtGiftPrice;
        private LinearLayout llGift;

        public GiftViewHolder(View itemView, Context context) {
            super(itemView);
            txtGiftName = (TextView) itemView.findViewById(R.id.txt_gift_name);
            imgGiftImage = (CircleImageView) itemView.findViewById(R.id.img_gift);
            txtGiftPrice = (TextView) itemView.findViewById(R.id.txt_gift_price);
            llGift = (LinearLayout) itemView.findViewById(R.id.ll_gift);
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
            return (c.getResources().getDisplayMetrics().widthPixels
                    - c.getResources().getDimensionPixelOffset(R.dimen._10dp) * 9) / 3;
        }
    }

    public void setGiftItemSelectListener(OnGiftItemSelectListener giftItemSelectListener) {
        this.giftItemSelectListener = giftItemSelectListener;
    }

    @FunctionalInterface
    public interface OnGiftItemSelectListener {
        void onGiftItemSelect(GiftItem giftItem);
    }
}
