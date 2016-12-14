package jp.newbees.mastersip.ui.dialog;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.SelectionAdapter;
import jp.newbees.mastersip.model.SelectionItem;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vietbq on 12/14/16.
 */

public class SelectionDialog extends BaseDialog {

    public static final String TAG = "SelectionDialog";
    public static final String LIST_SELECTION = "LIST SELECTION";
    private RecyclerView recyclerView;
    private List<SelectionItem> data;
    private SelectionAdapter adapter;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.selection_list_view);
        data = getArguments().getParcelableArrayList(LIST_SELECTION);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SelectionAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_selection;
    }
}
