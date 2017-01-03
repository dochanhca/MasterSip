package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.LocationItem;

/**
 * Created by ducpv on 12/23/16.
 */

public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<LocationItem> locationItems;

    /**
     * Interface  when click item on Location list
     */
    public interface OnLocationAdapterClick {
        /**
         * Callback when button select all pressed
         * @param id
         */
        void onSelectAllClick(int id);

        /**
         * Callback when item selected
         * @param position
         * @param isChecked
         */
        void onSelectItemClick(int position, boolean isChecked);
    }

    private OnLocationAdapterClick onLocationAdapterClick;

    public void setOnLocationAdapterClick(OnLocationAdapterClick onLocationAdapterClick) {
        this.onLocationAdapterClick = onLocationAdapterClick;
    }

    public LocationAdapter(Context context, List<LocationItem> locationItems) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.locationItems = locationItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LocationItem.CHILD) {
            View view = inflater.inflate(R.layout.item_child_location, parent, false);

            final ChildViewHolder viewHolder = new ChildViewHolder(context, view);

            viewHolder.cbSelectArea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = viewHolder.getAdapterPosition();
                    onLocationAdapterClick.onSelectItemClick(position, isChecked);
                }
            });

            return viewHolder;
        } else {
            View view = inflater.inflate(R.layout.item_parent_location, parent, false);

            ParentViewHolder viewHolder = new ParentViewHolder(context, view);

            setupClickableViews(viewHolder);

            return viewHolder;
        }
    }

    private void setupClickableViews(final ParentViewHolder viewHolder) {
        viewHolder.btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationItem locationItem = locationItems.get(viewHolder.getAdapterPosition());
                onLocationAdapterClick.onSelectAllClick(locationItem.getSelectionItem().getId());
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LocationItem locationItem = locationItems.get(position);

        if (holder.getItemViewType() == LocationItem.CHILD) {
            ChildViewHolder childViewHolder = (ChildViewHolder) holder;
            childViewHolder.bindChildView(locationItem, position, locationItems.size());

        } else {
            ParentViewHolder parentViewHolder = (ParentViewHolder) holder;
            parentViewHolder.bindParentView(locationItem);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return locationItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return locationItems.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView txtDistrictName;
        private CheckBox cbSelectArea;
        private ViewGroup parentView;

        public ChildViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            txtDistrictName = (TextView) itemView.findViewById(R.id.txt_district_name);
            cbSelectArea = (CheckBox) itemView.findViewById(R.id.cb_select);
            parentView = (ViewGroup) itemView.findViewById(R.id.parent_view);
        }

        public void bindChildView(LocationItem locationItem, int position, int itemSize) {
            txtDistrictName.setText(locationItem.getSelectionItem().getTitle());
            cbSelectArea.setChecked(locationItem.isChecked());

            /**
             * Add corner for some rows
             */
            if (position == 0 || position == itemSize - 1) {
                parentView.setBackground(context.getResources().getDrawable(R.drawable.bg_corner_layout));
            } else if (position == LocationItem.START_CHINA || position == LocationItem.START_KINKI
                    || position == LocationItem.START_KONTO || position == LocationItem.START_MIDDLE
                    || position == LocationItem.START_KYUSHU || position == LocationItem.START_NORTHEAST
                    || position == LocationItem.START_SHIKOKU) {
                parentView.setBackground(context.getResources().getDrawable(R.drawable.bg_corner_header_layout));
            } else if (position == LocationItem.END_CHINA || position == LocationItem.END_KINKI
                    || position == LocationItem.END_KONTO || position == LocationItem.END_KYUSHU
                    || position == LocationItem.END_MIDDLE || position == LocationItem.END_NORTHEAST
                    || position == LocationItem.END_SHIKOKU) {
                parentView.setBackground(context.getResources().getDrawable(R.drawable.bg_corner_bottom_layout));
            }

        }
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCityName;
        private ImageView btnSelectAll;

        public ParentViewHolder(Context context, View itemView) {
            super(itemView);
            txtCityName = (TextView) itemView.findViewById(R.id.txt_city_name);
            btnSelectAll = (ImageView) itemView.findViewById(R.id.btn_select_all);
        }

        public void bindParentView(LocationItem locationItem) {
            txtCityName.setText(locationItem.getSelectionItem().getTitle());

        }
    }
}
