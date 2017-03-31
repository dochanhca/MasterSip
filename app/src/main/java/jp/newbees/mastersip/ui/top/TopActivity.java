package jp.newbees.mastersip.ui.top;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.event.RoomChatEvent;
import jp.newbees.mastersip.fcm.MyFirebaseMessagingService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.CallCenterFinishedCallActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.gift.ListGiftFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends CallCenterFinishedCallActivity implements View.OnClickListener, TopPresenter.TopPresenterListener, BaseActivity.BottomNavigation {

    private static final String TAG = "TopActivity";
    private TopPresenter topPresenter;
    private static final int SEARCH_FRAGMENT = 0;
    private static final int CHAT_GROUP_FRAGMENT = 1;
    private static final int FOOT_PRINT_FRAGMENT = 2;
    private static final int FLOW_FRAGMENT = 3;
    private static final int MY_MENU_CONTAINER_FRAGMENT = 4;

    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;

    private NavigationLayoutGroup.OnChildItemClickListener mOnNavigationChangeListener = new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {
            viewPager.setCurrentItem(position, false);
            ConfigManager.getInstance().setCurrentTabInRootNavigater(position);
        }
    };
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == MY_MENU_CONTAINER_FRAGMENT) {
                MyMenuContainerFragment fragment = (MyMenuContainerFragment) getFragmentForPosition(position);
                if (null != fragment) fragment.onTabSelected();
            }
            navigationLayoutGroup.setSelectedItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_top;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        topPresenter = new TopPresenter(this, this);
        navigationLayoutGroup.setOnChildItemClickListener(mOnNavigationChangeListener);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        fillData();
        topPresenter.requestPermissions();
        getDataFromFCMChatMessage(getIntent());
    }

    private void getDataFromFCMChatMessage(Intent intent) {
        if (intent.getExtras() != null) {
            UserItem fromUser = intent.getExtras().getParcelable(MyFirebaseMessagingService.FROM_USER);
            ChatActivity.startChatActivity(this, fromUser);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e(TAG, "Destroying helper.");
        if (getIabHelper() != null) {
            topPresenter.disposeIabHelper();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getIabHelper() == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (!getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.e(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromFCMChatMessage(intent);
        int position = ConfigManager.getInstance().getCurrentTabInRootNavigater();
        viewPager.setCurrentItem(position, false);
        navigationLayoutGroup.setSelectedItem(position);
    }

    private IabHelper getIabHelper() {
        return topPresenter.getIabHelper();
    }

    /**
     * @param roomChatEvent
     */
    @Subscribe
    public void onRoomChatEvent(RoomChatEvent roomChatEvent) {
        setUnreadMessageValue(roomChatEvent.getNumberOfRoomUnRead());
    }

    private void fillData() {
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        topPresenter.sendPurchaseResultToServer(TopPresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String transection) {
        TopPresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = TopPresenter.PurchaseStatus.NOT_SUCCESS;
            topPresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = TopPresenter.PurchaseStatus.FAIL;
            topPresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        backToMyMenuFragment(getSupportFragmentManager());
        showMessageDialog(String.format(getString(R.string.purchase_success), point + ""));
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    private void backToMyMenuFragment(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i <= count - 1; i++) {
                fragmentManager.popBackStackImmediate();
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SEARCH_FRAGMENT:
                    return SearchContainerFragment.newInstance();
                case CHAT_GROUP_FRAGMENT:
                    return ChatGroupFragment.newInstance();
                case FOOT_PRINT_FRAGMENT:
                    return FootPrintFragment.newInstance();
                case FLOW_FRAGMENT:
                    return FollowFragment.newInstance();
                case MY_MENU_CONTAINER_FRAGMENT:
                    return MyMenuContainerFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return navigationLayoutGroup.getChildCount();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, final int[] grantResults) {
        topPresenter.requestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_search_container);
        if (fragment != null && fragment instanceof ListGiftFragment) {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * @param containerViewId the ViewPager this adapter is being supplied to
     * @param id              pass in getItemId(position) as this is whats used internally in this class
     * @return the tag used for this pages fragment
     */
    public static String makeFragmentName(int containerViewId, long id) {
        return "android:switcher:" + containerViewId + ":" + id;
    }

    /**
     * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
     * yet OR is a sibling covered by {@link android.support.v4.view.ViewPager#setOffscreenPageLimit(int)}. Can use this to call methods on
     * the current positions fragment.
     */
    @Nullable
    public Fragment getFragmentForPosition(int position) {
        String tag = makeFragmentName(viewPager.getId(), position);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment;
    }

    public TopPresenter getPresenter() {
        return topPresenter;
    }

    public void showSearchFragment() {
        viewPager.setCurrentItem(0, false);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
