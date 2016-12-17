package jp.newbees.mastersip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.SelectionAdapter;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vietbq on 12/14/16.
 */

public class SelectionDialog extends BaseDialog implements SelectionAdapter.OnSelectionAdapterClick {

    public static final String TAG = "SelectionDialog";
    public static final String LIST_SELECTION = "LIST SELECTION";
    public static final String DIALOG_TILE = "DIALOG_TILE";
    private RecyclerView recyclerView;
    private List<SelectionItem> data;
    private String title;
    private SelectionAdapter adapter;

    private int selectedItem;

    public interface OnSelectionDialogClick {
        void onItemSelected(int position);
    }

    private SelectionDialog.OnSelectionDialogClick onSelectionDialogClick;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.selection_list_view);
        data = getArguments().getParcelableArrayList(LIST_SELECTION);
        title = getArguments().getString(DIALOG_TILE);

        initRecyclerView();

        setDialogHeader(title);

        adapter.setOnSelectionAdapterClick(this);

        setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectionDialogClick.onItemSelected(selectedItem);
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = (int) (Utils.getScreenWidth(getApplicationContext()) * 0.9);
        int height = (int) (Utils.getScreenHeight(getApplicationContext()) * 0.6);

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width,
                    height);
        }
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_selection;
    }

    @Override
    public void onItemSelected(int position) {
        selectedItem = position;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onSelectionDialogClick = (OnSelectionDialogClick) context;
        } catch (ClassCastException e) {
            //
        }
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SelectionAdapter(getActivity().getApplicationContext(), data);
        recyclerView.setAdapter(adapter);
    }

}
