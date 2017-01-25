package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterViewPagerProfileDetail;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 1/5/17.
 */

public class ProfileDetailFragment extends BaseFragment {

    @BindView(R.id.view_pager_profile)
    ViewPager viewPagerProfile;
    @BindView(R.id.img_previous)
    ImageView imgPrevious;
    @BindView(R.id.img_next)
    ImageView imgNext;

    private static final String USER_ITEMS = "USER_ITEMS";
    private static final String POSITION = "POSITION";

    private UserItem userItem;
    private List<UserItem> userItemList;
    private int currentIndex;

    private ViewPager.OnPageChangeListener onPagerProfileChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // unused
        }

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;
            updatePagerIndicator();
            setFragmentTitle(userItemList.get(position).getUsername());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // unused
        }
    };

    public static ProfileDetailFragment newInstance(List<UserItem> userItems, int position) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ProfileDetailFragment.USER_ITEMS,
                (ArrayList<? extends Parcelable>) userItems);
        args.putInt(ProfileDetailFragment.POSITION, position);

        ProfileDetailFragment fragment = new ProfileDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_profile_detail;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        userItemList = getArguments().getParcelableArrayList(USER_ITEMS);
        currentIndex = getArguments().getInt(POSITION);

        userItem = userItemList.get(currentIndex);

        ButterKnife.bind(this, mRoot);
        setFragmentTitle(userItem.getUsername());

        initViewPagerProfile();
    }

    @OnClick({R.id.img_back, R.id.img_previous, R.id.img_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.img_previous:
                onBackwardClick();
                break;
            case R.id.img_next:
                onForwardClick();
                break;
            default:
                break;
        }
    }

    private void initViewPagerProfile() {
        AdapterViewPagerProfileDetail adapterViewPagerProfileDetail = new AdapterViewPagerProfileDetail(getChildFragmentManager(),
                userItemList);
        viewPagerProfile.setAdapter(adapterViewPagerProfileDetail);
        viewPagerProfile.addOnPageChangeListener(onPagerProfileChangeListener);
        viewPagerProfile.setCurrentItem(currentIndex);
        updatePagerIndicator();
    }

    private void onForwardClick() {
        if (currentIndex < userItemList.size() - 1) {
            currentIndex++;
            viewPagerProfile.setCurrentItem(currentIndex);
        }
    }

    private void onBackwardClick() {
        if (currentIndex >= 1) {
            currentIndex--;
            viewPagerProfile.setCurrentItem(currentIndex);
        }
    }

    private void updatePagerIndicator() {
        if (userItemList.size() <= 1) {
            imgPrevious.setVisibility(View.INVISIBLE);
            imgNext.setVisibility(View.INVISIBLE);
            return;
        }

        imgNext.setVisibility(currentIndex == userItemList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
        imgPrevious.setVisibility(currentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
    }
}


