package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 5/30/17.
 */

public class NotificationListFragment extends BaseFragment {

    public static BaseFragment newInstance() {
        BaseFragment baseFragment = new NotificationListFragment();
        Bundle bundle = new Bundle();
        baseFragment.setArguments(bundle);
        return baseFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_notification_list;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        setFragmentTitle("Notification List");

    }
}
