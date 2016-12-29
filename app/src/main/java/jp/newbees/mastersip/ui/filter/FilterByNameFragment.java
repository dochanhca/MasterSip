package jp.newbees.mastersip.ui.filter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 12/6/16.
 */

public class FilterByNameFragment extends BaseFragment {

    private ImageView imgSearch;
    private ImageView imgClose;
    private EditText edtSearch;
    private RecyclerView recyclerUser;

    @Override
    protected int layoutId() {
        return R.layout.fragment_filter_by_name;
    }

    public static FilterByNameFragment newInstance() {
        return new FilterByNameFragment();
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle(getString(R.string.filter_by_name));

        imgSearch = (ImageView) mRoot.findViewById(R.id.img_search);
        imgClose = (ImageView) mRoot.findViewById(R.id.img_close);
        edtSearch = (EditText) mRoot.findViewById(R.id.edt_search);
        recyclerUser = (RecyclerView) mRoot.findViewById(R.id.recycler_user);
    }
}
