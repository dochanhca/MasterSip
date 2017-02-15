package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 2/14/17.
 */

public class OnlineListFragment extends BaseFragment {

    public static BaseFragment newInstance() {
        BaseFragment onlineListFragment = new OnlineListFragment();
        Bundle args = new Bundle();
        onlineListFragment.setArguments(args);
        return onlineListFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_online_list;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle("Temp Screen");
    }
}
