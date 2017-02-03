package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 1/23/17.
 */

public class FollowFragment extends BaseFragment {

    public static Fragment newInstance() {
        Fragment fragment = new FollowFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.flow_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        //TODO
    }
}
