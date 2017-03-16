package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.PaymentAdOnItem;

/**
 * Created by ducpv on 3/16/17.
 */

public class PaymentAdOnAdapter extends RecyclerView.Adapter<PaymentAdOnAdapter.ViewHolder> {

    private List<PaymentAdOnItem> data;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public PaymentAdOnAdapter(Context context, List<PaymentAdOnItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_ad_on, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentAdOnItem item = data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    public void add(PaymentAdOnItem item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<PaymentAdOnItem> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View root) {
            super(root);

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

