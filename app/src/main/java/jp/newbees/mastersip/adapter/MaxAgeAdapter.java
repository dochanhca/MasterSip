package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.AgeItem;

/**
 * Created by ducpv on 12/28/16.
 */

public class MaxAgeAdapter extends RecyclerView.Adapter<MaxAgeAdapter.ViewHolder> {


    private List<AgeItem> data;
    private Context context;

    private int selectedItem;
    OnMaxAgeAdapterClick onMaxAgeAdapterClick;

    @FunctionalInterface
    public interface OnMaxAgeAdapterClick {
        void onMaxAgeSelected(int position);
    }

    public MaxAgeAdapter(Context context, List<AgeItem> data, int selectedItem) {
        this.data = data;
        this.context = context;
        this.selectedItem = selectedItem;
    }

    public void setOnMaxAgeAdapterClick(OnMaxAgeAdapterClick onMaxAgeAdapterClick) {
        this.onMaxAgeAdapterClick = onMaxAgeAdapterClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_age, parent, false);

        final ViewHolder selectionHolder = new ViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = selectionHolder.getAdapterPosition();

                onMaxAgeAdapterClick.onMaxAgeSelected(position);
                notifyItemChanged(selectedItem);
                selectedItem = position;
                notifyItemChanged(selectedItem);
            }
        });

        return selectionHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AgeItem item = data.get(position);
        holder.txtTitle.setText(item.getSelectionItem().getTitle());

        holder.rootView.setClickable(item.isDisable() ? false : true);

        if (position == selectedItem) {
            holder.txtTitle.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.txtTitle.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ViewGroup rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.rootView = (ViewGroup) itemView.findViewById(R.id.root_view);
            this.txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        }
    }
}
