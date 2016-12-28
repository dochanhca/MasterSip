package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.filter.FilterFragment;
import jp.newbees.mastersip.ui.filter.FilterLocationFragment;

/**
 * Created by ducpv on 12/27/16.
 */

public class SearchContainerFragment extends BaseFragment {

    public static SearchContainerFragment newInstance() {
        SearchContainerFragment fragment = new SearchContainerFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_search_container;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSearchFragment();
    }

    private void showSearchFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_search_container, SearchFragment.newInstance(),
                SearchFragment.class.getName()).commit();
    }
}
