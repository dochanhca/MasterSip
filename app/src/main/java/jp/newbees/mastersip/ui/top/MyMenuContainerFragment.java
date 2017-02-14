package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.mymenu.MyMenuFragment;
import jp.newbees.mastersip.ui.mymenu.OnlineListFragment;

/**
 * Created by ducpv on 2/14/17.
 */

public class MyMenuContainerFragment extends BaseFragment {

    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";
    private static final int MY_MENU_FRAGMENT = 0;
    private int mCurrentlyShowingFragment;

    public static Fragment newInstance() {
        Fragment myMenuContainerFragment = new MyMenuContainerFragment();
        Bundle bundle = new Bundle();
        myMenuContainerFragment.setArguments(bundle);
        return myMenuContainerFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, mCurrentlyShowingFragment);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_my_menu_container;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            showMyMenu();
            mCurrentlyShowingFragment = MY_MENU_FRAGMENT;
        } else {
            mCurrentlyShowingFragment = savedInstanceState.getInt(CURRENT_FRAGMENT);
        }
    }

    private void showMyMenu() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_my_menu_container, MyMenuFragment.newInstance(),
                MyMenuFragment.class.getName()).commit();
    }

    public static void showSettingCallFragment(FragmentActivity activity, BaseFragment fragment) {
        SettingCallFragment settingFragment = SettingCallFragment.newInstance();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_my_menu_container, settingFragment,
                SettingCallFragment.class.getName()).commit();
    }

    public static void showOnlineListFragment(FragmentActivity activity, BaseFragment fragment) {
        Fragment onlineListFragment = OnlineListFragment.newInstance();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_my_menu_container, onlineListFragment,
                SettingCallFragment.class.getName()).commit();
    }

    public final void onTabSelected() {
        MyMenuFragment fragment = (MyMenuFragment) getFragmentManager().
                findFragmentByTag(MyMenuFragment.class.getName());
        if (null != fragment) fragment.onTabSelected();
    }
}
