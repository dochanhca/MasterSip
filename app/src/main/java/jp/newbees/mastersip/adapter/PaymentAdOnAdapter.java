package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.utils.Constant;

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
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                onItemClickListener.onItemClick(position);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentAdOnItem item = data.get(position);

        holder.txtPoint.setText(item.getPoint() + context.getString(R.string.pt));
        holder.btnPrice.setText(Currency.getInstance(Locale.JAPAN).getSymbol() + " " +
                Constant.CURRENCY_FORMAT.format(item.getCash()));
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

    public List<PaymentAdOnItem> getData() {
        return this.data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtPoint;
        private Button btnPrice;
        private RelativeLayout rootView;

        public ViewHolder(View root) {
            super(root);

            txtPoint = (TextView) root.findViewById(R.id.txt_point);
            btnPrice = (Button) root.findViewById(R.id.btn_price);
            rootView = (RelativeLayout) root.findViewById(R.id.root_view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

