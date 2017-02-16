package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.mailbackup.CheckCodeFragment;
import jp.newbees.mastersip.ui.mailbackup.RegisterEmailBackupFragment;
import jp.newbees.mastersip.ui.mymenu.MyMenuFragment;
import jp.newbees.mastersip.ui.mymenu.OnlineListFragment;

/**
 * Created by thangit14 on 2/14/17.
 */

public class MyMenuContainerFragment extends BaseFragment {

    public static MyMenuContainerFragment newInstance() {
        return new MyMenuContainerFragment();
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
            showMyMenuFragment();
        }
    }

    private void showMyMenuFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_my_menu_container, MyMenuFragment.newInstance(),
                MyMenuFragment.class.getName()).commit();
    }

    public static void showRegisterEmailBackupFragment(FragmentActivity activity) {
        BaseFragment currentFragment = RegisterEmailBackupFragment.newInstance();
        performShowFragment(activity, currentFragment, RegisterEmailBackupFragment.class.getName());
    }

    public static void showCheckCodeFragment(FragmentActivity activity, CheckCodeFragment.CallFrom callFrom) {
        BaseFragment currentFragment = CheckCodeFragment.newInstance(callFrom);
        performShowFragment(activity, currentFragment, CheckCodeFragment.class.getName());
    }

    public static void showSettingCallFragment(FragmentActivity activity) {
        BaseFragment fragment = SettingCallFragment.newInstance();
        performShowFragment(activity,fragment,SettingCallFragment.class.getName());
    }

    public static void showOnlineListFragment(FragmentActivity activity) {
        BaseFragment fragment = OnlineListFragment.newInstance();
        performShowFragment(activity,fragment,OnlineListFragment.class.getName());

    }

    private static void performShowFragment(FragmentActivity activity, BaseFragment fragment, String name) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_my_menu_container, fragment, name).commit();
    }

    public final void onTabSelected() {
        MyMenuFragment fragment = (MyMenuFragment) getFragmentManager().
                findFragmentByTag(MyMenuFragment.class.getName());
        if (null != fragment)
            fragment.onTabSelected();
    }
}
