package jp.newbees.mastersip.adapter;

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

    public SelectionAdapter(List<SelectionItem> data) {
        this.data = data;
    }

    @Override
    public SelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection, parent, false);
        return new SelectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectionHolder holder, int position) {
        SelectionItem item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SelectionHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;

        public SelectionHolder(View itemView) {
            super(itemView);
            this.txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        }
    }
}
