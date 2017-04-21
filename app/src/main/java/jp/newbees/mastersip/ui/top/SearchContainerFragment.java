package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.gift.ListGiftFragment;
import jp.newbees.mastersip.ui.profile.SettingOnlineFragment;

/**
 * Created by ducpv on 12/27/16.
 */

public class SearchContainerFragment extends BaseFragment {

    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";
    private static final int SEARCH_FRAGMENT = 0;
    private int mCurrentlyShowingFragment;

    public static SearchContainerFragment newInstance() {
        SearchContainerFragment fragment = new SearchContainerFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, mCurrentlyShowingFragment);
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
        if (savedInstanceState == null) {
            showSearchFragment();
            mCurrentlyShowingFragment = SEARCH_FRAGMENT;
        } else {
            mCurrentlyShowingFragment = savedInstanceState.getInt(CURRENT_FRAGMENT);
        }

    }

    private void showSearchFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_search_container, SearchFragment.newInstance(),
                SearchFragment.class.getName()).commit();
    }

    public static void showSettingOnlineFragment(FragmentActivity activity, UserItem userItem) {
        BaseFragment settingOnlineFragment = SettingOnlineFragment.newInstance(userItem);
        showFragment(activity, settingOnlineFragment);
    }

    public static void showGiftFragment(FragmentActivity activity, UserItem userItem,
                                        int from,boolean needShowActionBar) {
        BaseFragment giftFragment = ListGiftFragment.newInstance(userItem,
                from, needShowActionBar);
        showFragment(activity, giftFragment);
    }

    private static void showFragment(FragmentActivity activity, BaseFragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, fragment).commit();
    }
}
