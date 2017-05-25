package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.mailbackup.ChangeEmailBackupFragment;
import jp.newbees.mastersip.ui.mailbackup.CheckCodeFragment;
import jp.newbees.mastersip.ui.mailbackup.RegisterEmailBackupFragment;
import jp.newbees.mastersip.ui.mymenu.BlockListFragment;
import jp.newbees.mastersip.ui.mymenu.HistoryCallFragment;
import jp.newbees.mastersip.ui.mymenu.MyMenuFragment;
import jp.newbees.mastersip.ui.mymenu.OnlineListFragment;
import jp.newbees.mastersip.ui.mymenu.SettingPushFragment;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 2/14/17.
 */

public class MyMenuContainerFragment extends BaseFragment {

    public static MyMenuContainerFragment newInstance() {
        MyMenuContainerFragment fragment = new MyMenuContainerFragment();
        return fragment;
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
            showMyMenuFragment(getActivity());
        }
    }

    private static void showMyMenuFragment(FragmentActivity activity) {
        BaseFragment fragment = MyMenuFragment.newInstance();
        performShowFragment(activity, fragment, false);
    }

    public static void showRegisterEmailBackupFragment(FragmentActivity activity) {
        BaseFragment fragment = RegisterEmailBackupFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    private static void showWebViewFragment(FragmentActivity activity) {
        BaseFragment fragment = PaymentFragment.newInstance(false);
        performShowFragment(activity, fragment);
    }

    public static void showChosePaymentTypeFragment(FragmentActivity activity) {
        String url = Utils.getURLChosePaymentType();
        Logger.e("showChosePaymentTypeFragment", url);
        showWebViewFragment(activity);
    }

    public static void showChangeEmailBackupFragment(FragmentActivity activity) {
        BaseFragment fragment = ChangeEmailBackupFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public static void showCheckCodeFragmentFromRegisterEmailBackUp(FragmentActivity activity) {
        BaseFragment fragment = CheckCodeFragment.newInstanceFromRegisterBackupEmail();
        performShowFragment(activity, fragment);
    }

    public static void showCheckCodeFragmentFromChangeEmailBackUp(FragmentActivity activity,
                                                                  String email) {
        BaseFragment fragment = CheckCodeFragment.newInstanceFromChangeBackupEmail(email);
        performShowFragment(activity, fragment);
    }

    private static void showFragmentAndAddToBackStack(FragmentActivity activity, BaseFragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_my_menu_container, fragment).commit();
    }

    public static void showSettingCallFragment(FragmentActivity activity) {
        BaseFragment fragment = SettingCallFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public static void showOnlineListFragment(FragmentActivity activity) {
        BaseFragment fragment = OnlineListFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public static void showHistoryCallFragment(FragmentActivity activity) {
        BaseFragment fragment = HistoryCallFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public static void showSettingPushFragment(FragmentActivity activity) {
        BaseFragment fragment = SettingPushFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public static void showBlockListFragment(FragmentActivity activity) {
        BaseFragment fragment = BlockListFragment.newInstance();
        performShowFragment(activity, fragment);
    }

    public final void onTabSelected() {
        MyMenuFragment fragment = (MyMenuFragment) getFragmentManager().
                findFragmentByTag(MyMenuFragment.class.getName());

        if (null != fragment) {
            fragment.onTabSelected();
        }
    }

    /**
     * show fragment and add to back stack
     *
     * @param activity
     * @param fragment
     */
    private static void performShowFragment(FragmentActivity activity, BaseFragment fragment) {
        performShowFragment(activity, fragment, true);
    }

    /**
     * @param activity
     * @param fragment
     * @param addToBackStack
     */
    private static void performShowFragment(FragmentActivity activity, BaseFragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            showFragmentAndAddToBackStack(activity, fragment);
        } else {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_my_menu_container, fragment, MyMenuFragment.class.getName()).commit();
        }
    }
}
