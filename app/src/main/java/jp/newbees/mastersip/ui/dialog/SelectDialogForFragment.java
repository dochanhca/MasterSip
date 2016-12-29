package jp.newbees.mastersip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.SelectionAdapter;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ducpv on 12/29/16.
 */

public class SelectDialogForFragment extends BaseDialog implements SelectionAdapter.OnSelectionAdapterClick {

    private static final String TAG = "SelectionDialog";
    private static final String LIST_SELECTION = "LIST SELECTION";
    private static final String DIALOG_TILE = "DIALOG_TILE";
    private static final String MODE = "MODE";
    private static final int FROM_FRAGMENT = 1;
    private static final int FROM_ACTIVITY = 2;
    private RecyclerView recyclerView;
    private List<SelectionItem> data;
    private String title;
    private SelectionAdapter adapter;

    private int mode;

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
        mode = getArguments().getInt(MODE);

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mode == FROM_FRAGMENT) {
            try {
                this.onSelectionDialogClick = (SelectionDialog.OnSelectionDialogClick) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = (int) (Utils.getScreenWidth(getApplicationContext()) * 0.9);
        int height = (int) (Utils.getScreenHeight(getApplicationContext()) * 0.6);

        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();

        lp.width = width;

        Dialog dialog = getDialog();
        if (dialog != null) {
            if (dialog.getWindow().getDecorView().getHeight() >= height) {
                lp.height = height;
            }
            dialog.getWindow().setAttributes(lp);
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
        if (mode == FROM_ACTIVITY) {

            try {
                this.onSelectionDialogClick = (SelectionDialog.OnSelectionDialogClick) context;
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling Activity must implement DialogClickListener interface");
            }
        }
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SelectionAdapter(getActivity().getApplicationContext(), data);
        recyclerView.setAdapter(adapter);
    }

    public static void openSelectionDialogFromFragment(Fragment fragment, int requestCode,
                                                       FragmentManager fragmentManager, ArrayList<SelectionItem> sortConditions, String title) {
        SelectionDialog selectionDialog = new SelectionDialog();

        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_TILE, title);
        bundle.putParcelableArrayList(LIST_SELECTION, sortConditions);
        bundle.putInt(MODE, FROM_FRAGMENT);

        selectionDialog.setArguments(bundle);
        selectionDialog.setTargetFragment(fragment, requestCode);

        selectionDialog.show(fragmentManager, "SelectionDialog");
    }
}
