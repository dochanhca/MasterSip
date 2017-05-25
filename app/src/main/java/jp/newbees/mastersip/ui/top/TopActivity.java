package jp.newbees.mastersip.ui.top;

import android.content.Context;
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
import jp.newbees.mastersip.model.MasterDataItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.CallPresenter;
import jp.newbees.mastersip.presenter.InAppPurchasePresenter;
import jp.newbees.mastersip.presenter.top.TopActivityPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.CallActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.gift.ListGiftFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends CallActivity implements
        View.OnClickListener, InAppPurchasePresenter.InAppPurchaseListener, BaseActivity.BottomNavigation, TopActivityPresenter.TopActivityListener {

    private static final String TAG = "TopActivity";
    private static final int REQUEST_OPEN_CALLER_PROFILE = 33;
    private static final int SEARCH_FRAGMENT = 0;
    private static final int CHAT_GROUP_FRAGMENT = 1;
    public static final int FOOT_PRINT_FRAGMENT = 2;
    public static final int FOLLOW_FRAGMENT = 3;
    public static final int MY_MENU_CONTAINER_FRAGMENT = 4;
    private static final String NAVIGATE_TO_FRAGMENT = "NAVIGATE_TO_FRAGMENT";

    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;

    private InAppPurchasePresenter inAppPurchasePresenter;
    private TopActivityPresenter topActivityPresenter;

    private UserItem caller;

    private NavigationLayoutGroup.OnChildItemClickListener mOnNavigationChangeListener = new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {
            viewPager.setCurrentItem(position, false);
            ConfigManager.getInstance().setCurrentTabInRootNavigater(position);
            popBackToFirstFragmentAndReload(position);
        }

        private void popBackToFirstFragmentAndReload(int position) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            navigationLayoutGroup.setSelectedItem(position);
            BaseFragment baseFragment = (BaseFragment) getFragmentForPosition(position);
            if (baseFragment == null) {
                return;
            }
            if (position == MY_MENU_CONTAINER_FRAGMENT) {
                MyMenuContainerFragment fragment = (MyMenuContainerFragment) baseFragment;
                if (null != fragment) fragment.onTabSelected();
            } else if (position == FOOT_PRINT_FRAGMENT) {
                FootPrintFragment footPrintFragment = (FootPrintFragment) getFragmentForPosition(position);
                footPrintFragment.initData();

            } else if (position == FOLLOW_FRAGMENT) {
                FollowFragment followFragment = (FollowFragment) getFragmentForPosition(position);
                followFragment.loadData();
            }
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
        inAppPurchasePresenter = new InAppPurchasePresenter(this, this);
        topActivityPresenter = new TopActivityPresenter(this, this);
        navigationLayoutGroup.setOnChildItemClickListener(mOnNavigationChangeListener);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        fillData();
        topActivityPresenter.requestPermissions();
        topActivityPresenter.loadMasterData();
        getDataFromFCMChatMessage(getIntent());
        ConfigManager.getInstance().setCurrentTabInRootNavigater(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_search_container);
        if (fragment != null && fragment instanceof ListGiftFragment) {
            getSupportFragmentManager().popBackStack();
        }
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
            inAppPurchasePresenter.disposeIabHelper();
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
        int position = intent.getIntExtra(NAVIGATE_TO_FRAGMENT, -1);
        if (position >= 0) {
            viewPager.setCurrentItem(position, false);
        } else {
            getDataFromFCMChatMessage(intent);
            position = ConfigManager.getInstance().getCurrentTabInRootNavigater();
            viewPager.setCurrentItem(position, false);
            navigationLayoutGroup.setSelectedItem(position);
        }
    }

    /**
     * @param roomChatEvent
     */
    @Subscribe
    public void onRoomChatEvent(RoomChatEvent roomChatEvent) {
        ConfigManager.getInstance().setUnreadMessage(roomChatEvent.getNumberOfRoomUnRead());
        setBudgieMessage(roomChatEvent.getNumberOfRoomUnRead());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        inAppPurchasePresenter.sendPurchaseResultToServer(InAppPurchasePresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String transection) {
        InAppPurchasePresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = InAppPurchasePresenter.PurchaseStatus.NOT_SUCCESS;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = InAppPurchasePresenter.PurchaseStatus.FAIL;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        backToMyMenuFragment(getSupportFragmentManager());
        showMessageDialog(String.format(getString(R.string.purchase_success), Integer.toString(point)));
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void onLoadMasterDataSuccess(MasterDataItem masterDataItem) {
        topActivityPresenter.saveCoin(masterDataItem.getCoin());
        setBudgieMessage(masterDataItem.getTotalChat());
        setBudgieFootPrint(masterDataItem.getTotalFootPrint());
        setBudgieFollower(masterDataItem.getTotalFollower());

        Log.d(TAG, "onLoadMasterDataSuccess: " + masterDataItem.getTotalFootPrint() + "-" + masterDataItem.getTotalFollower());
    }

    @Override
    public void onLoadMasterDataError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, final int[] grantResults) {
        topActivityPresenter.requestPermissions();
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        super.onTextDialogOkClick(requestCode);
        if (requestCode == REQUEST_OPEN_CALLER_PROFILE) {
            ProfileDetailItemActivity.startActivity(this, caller);
        }
    }

    public static final void navigateToFragment(Context context, int position) {
        Intent intent = new Intent(context, TopActivity.class);
        intent.putExtra(NAVIGATE_TO_FRAGMENT, position);
        context.startActivity(intent);
    }

    private IabHelper getIabHelper() {
        return inAppPurchasePresenter.getIabHelper();
    }

    private void fillData() {
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
    }

    private void backToMyMenuFragment(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i <= count - 1; i++) {
                fragmentManager.popBackStackImmediate();
            }
        }
    }

    private void getDataFromFCMChatMessage(Intent intent) {
        if (intent.getExtras() != null) {
            UserItem fromUser = intent.getExtras().getParcelable(MyFirebaseMessagingService.FROM_USER);
            int pushType = intent.getExtras().getInt(MyFirebaseMessagingService.PUSH_TYPE, -1);
            handlePushMessage(fromUser, pushType);
        }
    }

    private void handlePushMessage(UserItem fromUser, int pushType) {
        switch (pushType) {
            case MyFirebaseMessagingService.PUSH_MISS_CALL:
                showMessageDialogForMissedCall(fromUser);
                break;
            case MyFirebaseMessagingService.PUSH_CHAT:
                ChatActivity.startChatActivity(this, fromUser);
                break;
            case MyFirebaseMessagingService.PUSH_FOLLOWED:
                viewPager.setCurrentItem(FOLLOW_FRAGMENT, false);
                ConfigManager.getInstance().setCurrentTabInRootNavigater(FOLLOW_FRAGMENT);
                break;
            default:
                break;
        }
    }

    private void showMessageDialogForMissedCall(UserItem fromUser) {
        this.caller = fromUser;
        StringBuilder content = new StringBuilder();
        content.append(fromUser.getUsername()).append(getString(R.string.from)).append("\n")
                .append(getString(R.string.i_done_with_incoming_call)).append("\n")
                .append(getString(R.string.would_you_like_to_check_profile));
        String positiveTitle = getString(R.string.check);
        String negativeTitle = getString(R.string.do_not_check);

        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_OPEN_CALLER_PROFILE)
                .setPositiveTitle(positiveTitle).setNegativeTitle(negativeTitle)
                .build(content.toString());
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
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
                case FOLLOW_FRAGMENT:
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

    public InAppPurchasePresenter getPresenter() {
        return inAppPurchasePresenter;
    }

    public void showSearchFragment() {
        viewPager.setCurrentItem(0, false);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(int type) {
        super.didSendMsgRequestEnableSettingCall(type);
        TextDialog textDialog = new TextDialog.Builder()
                .hideNegativeButton(true)
                .build(CallPresenter.getMessageSendRequestSuccess(getApplicationContext(), getCurrentCallee(), type));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        super.didSendMsgRequestEnableSettingCallError(errorMessage, errorCode);
    }
}
