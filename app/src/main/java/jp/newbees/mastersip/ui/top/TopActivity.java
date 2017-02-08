package jp.newbees.mastersip.ui.top;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.event.RoomChatEvent;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends CallCenterActivity implements View.OnClickListener, TopPresenter.TopView, BaseActivity.BottomNavigation {
    public static final int PERMISSIONS_REQUEST_CAMERA = 202;
    public static final int PERMISSIONS_ENABLED_CAMERA = 203;
    public static final int PERMISSIONS_ENABLED_MIC = 204;
    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 205;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 206;

    private static final String TAG = "TopActivity";
    private TopPresenter topPresenter;
    private static final int SEARCH_FRAGMENT = 0;
    private static final int CHAT_GROUP_FRAGMENT = 1;
    private static final int FOOT_PRINT_FRAGMENT = 2;
    private static final int FLOW_FRAGMENT = 3;
    private static final int MY_MENU_FRAGMENT = 4;

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
            if (position == MY_MENU_FRAGMENT) {
                MyMenuFragment fragment = (MyMenuFragment) getFragmentForPosition(position);
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
        topPresenter = new TopPresenter(getApplicationContext(), this);
        navigationLayoutGroup.setOnChildItemClickListener(mOnNavigationChangeListener);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        fillData();
        topPresenter.requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int position = ConfigManager.getInstance().getCurrentTabInRootNavigater();
        viewPager.setCurrentItem(position, false);
        navigationLayoutGroup.setSelectedItem(position);
    }

    /**
     *
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
                case MY_MENU_FRAGMENT:
                    return MyMenuFragment.newInstance();
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                topPresenter.didGrantedCameraPermission();
                break;
            case PERMISSIONS_ENABLED_CAMERA:
//                disableVideo(grantResults[0] != PackageManager.PERMISSION_GRANTED);
                break;
            case PERMISSIONS_ENABLED_MIC:
                break;
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
    public
    @Nullable
    Fragment getFragmentForPosition(int position) {
        String tag = makeFragmentName(viewPager.getId(), position);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment;
    }
}
