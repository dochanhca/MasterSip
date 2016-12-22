package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.myprofile.MyInfoFragment;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends BaseActivity implements View.OnClickListener {

    private static final int SEARCH_FRAGMENT = 0;
    private static final int CHAT_GROUP_FRAGMENT = 1;
    private static final int FOOT_PRINT_FRAGMENT = 2;
    private static final int FLOW_FRAGMENT = 3;
    private static final int PROFILE_FRAGMENT = 4;

    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;

    private NavigationLayoutGroup navigationLayoutGroup;
    private NavigationLayoutGroup.OnChildItemClickListener mOnNavigationChangeListener = new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {
            viewPager.setCurrentItem(position, true);
        }
    };
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
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
        initHeader(getString(R.string.top_activity));
        navigationLayoutGroup = (NavigationLayoutGroup) findViewById(R.id.navigation_bar);
        navigationLayoutGroup.setOnChildItemClickListener(mOnNavigationChangeListener);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        fillData();
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
                    return SearchFragment.newInstance();
                case CHAT_GROUP_FRAGMENT:
                    return ChatGroupFragment.newInstance();
                case FOOT_PRINT_FRAGMENT:
                    return FootPrintFragment.newInstance();
                case FLOW_FRAGMENT:
                    return FlowFragment.newInstance();
                case PROFILE_FRAGMENT:
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
}
