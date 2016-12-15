package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SelectionItem;

/**
 * Created by vietbq on 12/14/16.
 */

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionHolder> {

    private List<SelectionItem> data;
    private Context context;

//    private int selectedItem;

    public interface OnSelectionAdapterClick {
        abstract void onItemSelected(int position);
    }

    private OnSelectionAdapterClick onSelectionAdapterClick;

    public SelectionAdapter(Context context, List<SelectionItem> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public SelectionHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection, parent, false);

        final SelectionHolder selectionHolder = new SelectionHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = selectionHolder.getAdapterPosition();
//                onSelectionAdapterClick.onItemSelected(position);
//                selectedItem = position;
            }
        });

        return new SelectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectionHolder holder, int position) {
        SelectionItem item = data.get(position);
        holder.txtTitle.setText(item.getTitle());

//        if (position == selectedItem) {
//            holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//        } else {
//            holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.white));
//        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SelectionHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ViewGroup rootView;

        public SelectionHolder(View itemView) {
            super(itemView);
            this.txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            this.rootView = (ViewGroup) itemView.findViewById(R.id.root_view);
        }
    }
}
