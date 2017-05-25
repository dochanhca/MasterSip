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
 * Created by vietbq on 12/14/16.
 */

public class SelectionDialog extends BaseDialog implements SelectionAdapter.OnSelectionAdapterClick {

    private static final String TAG = "SelectionDialog";
    private static final String LIST_SELECTION = "LIST SELECTION";
    private static final String DIALOG_TILE = "DIALOG_TILE";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private static final String POSITIVE_TITLE = "POSITIVE_TITLE";
    private RecyclerView recyclerView;
    private List<SelectionItem> data;
    private String title;
    private SelectionAdapter adapter;

    private int selectedItemIndex;
    private SelectionItem selectionItem;

    public interface OnSelectionDialogClick {
        void onItemSelected(int position);
    }

    private SelectionDialog.OnSelectionDialogClick onSelectionDialogClick;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.selection_list_view);
        data = getArguments().getParcelableArrayList(LIST_SELECTION);
        title = getArguments().getString(DIALOG_TILE);
        selectionItem = getArguments().getParcelable(SELECTED_ITEM);
        String positiveTitle = getArguments().getString(POSITIVE_TITLE, "");

        if (!"".equals(positiveTitle)) {
            setPositiveButtonContent(positiveTitle);
        }

        selectedItemIndex = data.indexOf(selectionItem);

        initRecyclerView();

        setDialogHeader(title);

        adapter.setOnSelectionAdapterClick(this);

        setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItemIndex >= 0) {
                    onSelectionDialogClick.onItemSelected(selectedItemIndex);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onSelectionDialogClick = (OnSelectionDialogClick) getTargetFragment();
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
        selectedItemIndex = position;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.onSelectionDialogClick = (OnSelectionDialogClick) context;
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling Activity must implement DialogClickListener interface");
            }
        }
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SelectionAdapter(getActivity().getApplicationContext(), data, selectedItemIndex);
        recyclerView.setAdapter(adapter);
    }

    public static void openSelectionDialogFromFragment(Fragment fragment, int requestCode,
                                                       FragmentManager fragmentManager,
                                                       ArrayList<SelectionItem> selectionItems,
                                                       String title,  String positiveTitle,
                                                       SelectionItem selectedItem) {
        SelectionDialog selectionDialog = new SelectionDialog();

        Bundle bundle = new Bundle();
        bundle.putString(SelectionDialog.DIALOG_TILE, title);
        bundle.putString(SelectionDialog.POSITIVE_TITLE, positiveTitle);
        bundle.putParcelableArrayList(SelectionDialog.LIST_SELECTION, selectionItems);
        bundle.putParcelable(SelectionDialog.SELECTED_ITEM, selectedItem);

        selectionDialog.setArguments(bundle);
        selectionDialog.setTargetFragment(fragment, requestCode);
        selectionDialog.show(fragmentManager, "SelectionDialog");
    }

    public static void openSelectionDialogFromActivity(FragmentManager fragmentManager,
                                                       ArrayList<SelectionItem> selectionItems,
                                                       String title, String positiveTitle,
                                                       SelectionItem selectedItem) {
        SelectionDialog selectionDialog = new SelectionDialog();

        Bundle bundle = new Bundle();
        bundle.putString(SelectionDialog.DIALOG_TILE, title);
        bundle.putString(SelectionDialog.POSITIVE_TITLE, positiveTitle);
        bundle.putParcelableArrayList(SelectionDialog.LIST_SELECTION, selectionItems);
        bundle.putParcelable(SelectionDialog.SELECTED_ITEM, selectedItem);

        selectionDialog.setArguments(bundle);
        selectionDialog.show(fragmentManager, "SelectionDialog");
    }
}
