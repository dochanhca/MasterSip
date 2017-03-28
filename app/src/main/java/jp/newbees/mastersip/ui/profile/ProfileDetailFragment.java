package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterViewPagerProfileDetail;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.call.BaseCenterOutgoingCallPresenter;
import jp.newbees.mastersip.presenter.profile.ProfileDetailPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 1/5/17.
 */

public class ProfileDetailFragment extends BaseFragment implements ProfileDetailPresenter.ProfileView,
        BaseCenterOutgoingCallPresenter.OutgoingCallListener {
    private static final int REQUEST_NOTIFY_NOT_ENOUGH_POINT = 1;
    private static final int REQUEST_NOTIFY_CALLEE_REJECT_CALL = 2;

    @BindView(R.id.view_pager_profile)
    ViewPager viewPagerProfile;
    @BindView(R.id.img_previous)
    ImageView imgPrevious;
    @BindView(R.id.img_next)
    ImageView imgNext;

    private static final String USER_ITEMS = "USER_ITEMS";
    private static final String POSITION = "POSITION";
    private static final String NEXT_PAGE = "NEXT_PAGE";
    private static final String TYPE_SEARCH = "TYPE_SEARCH";

    private List<UserItem> userItemList;
    private String nextPage;
    private int typeSearch;
    private int currentIndex = -1;
    private AdapterViewPagerProfileDetail adapterViewPagerProfileDetail;
    private ProfileDetailPresenter profileDetailPresenter;
    private boolean isLoadingMoreUser = false;

    private ViewPager.OnPageChangeListener onPagerProfileChangeListener = new ViewPager.OnPageChangeListener() {
        boolean lastPageChanged = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int lastIndex = adapterViewPagerProfileDetail.getCount() - 1;
            if (lastPageChanged && position == lastIndex && !isLoadingMoreUser && canLoadMoreUser()) {
                loadMoreUser();
            }
        }

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;
            updatePagerIndicator();
            setFragmentTitle(userItemList.get(position).getUsername());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int lastIndex = adapterViewPagerProfileDetail.getCount() - 1;
            lastPageChanged = (currentIndex == lastIndex && state == 1) ? true : false;
        }
    };

    public static ProfileDetailFragment newInstance(List<UserItem> userItems, int position,
                                                    String nextPage, int currentTypeSearch) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ProfileDetailFragment.USER_ITEMS,
                (ArrayList<? extends Parcelable>) userItems);
        args.putInt(ProfileDetailFragment.POSITION, position);
        args.putString(ProfileDetailFragment.NEXT_PAGE, nextPage);
        args.putInt(ProfileDetailFragment.TYPE_SEARCH, currentTypeSearch);

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
        profileDetailPresenter = new ProfileDetailPresenter(getActivity().getApplicationContext(), this, this);

        userItemList = getArguments().getParcelableArrayList(USER_ITEMS);
        currentIndex = getArguments().getInt(POSITION);
        nextPage = getArguments().getString(NEXT_PAGE);
        typeSearch = getArguments().getInt(TYPE_SEARCH);

        ButterKnife.bind(this, mRoot);
        setFragmentTitle(userItemList.get(currentIndex).getUsername());

        initViewPagerProfile();
        profileDetailPresenter.registerEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileDetailPresenter.unRegisterEvent();
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

    @Override
    public void didLoadMoreUser(Map<String, Object> data) {
        List<UserItem> temp = (List<UserItem>) data.get(FilterUserTask.LIST_USER);
        nextPage = (String) data.get(FilterUserTask.NEXT_PAGE);

        userItemList.addAll(temp);
        adapterViewPagerProfileDetail.notifyDataSetChanged();
        isLoadingMoreUser = false;
        disMissLoading();
        onForwardClick();
    }

    @Override
    public void didLoadUserError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        isLoadingMoreUser = false;
        disMissLoading();
    }

    @Override
    public void didCheckCallError(String errorMessage, int errorCode) {
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            showDialogNotifyNotEnoughPoint();
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
    }

    @Override
    public void outgoingVoiceCall(UserItem callee, String callID) {
        OutgoingVoiceActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void outgoingVideoCall(UserItem callee, String callID) {
        OutgoingVideoVideoActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void outgoingVideoChatCall(UserItem callee, String callID) {
        OutgoingVideoChatActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);

    }

    @Override
    public void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String message = busyCallEvent.getHandleName() + " " + getString(R.string.mess_callee_reject_call);
        String positiveTitle = getString(R.string.back_to_profile_detail);
        OneButtonDialog.showDialog(this, getFragmentManager(),
                REQUEST_NOTIFY_CALLEE_REJECT_CALL, "", message, "", positiveTitle);
    }

    private void showDialogNotifyNotEnoughPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        String title, content, positiveTitle;
        if (gender == UserItem.MALE) {
            title = getString(R.string.point_are_missing);
            content = getString(R.string.mess_suggest_buy_point);
            positiveTitle = getString(R.string.add_point);
        } else {
            title = getString(R.string.partner_point_are_missing);
            content = userItemList.get(currentIndex).getUsername() + getString(R.string.mess_suggest_missing_point_for_girl);
            positiveTitle = getString(R.string.to_attack);
        }
        TextDialog.openTextDialog(this, REQUEST_NOTIFY_NOT_ENOUGH_POINT, getFragmentManager(),
                content, title, positiveTitle, false);
    }

    private void initViewPagerProfile() {
        if (adapterViewPagerProfileDetail == null) {
            adapterViewPagerProfileDetail = new AdapterViewPagerProfileDetail(getChildFragmentManager(),
                    userItemList);
        } else {
            adapterViewPagerProfileDetail.notifyDataSetChanged();
        }
        viewPagerProfile.setAdapter(adapterViewPagerProfileDetail);
        viewPagerProfile.addOnPageChangeListener(onPagerProfileChangeListener);
        viewPagerProfile.setCurrentItem(currentIndex);
        updatePagerIndicator();
    }

    private void onForwardClick() {
        if (currentIndex < userItemList.size() - 1) {
            currentIndex++;
            viewPagerProfile.setCurrentItem(currentIndex);
        } else {
            loadMoreUser();
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
        imgPrevious.setVisibility(currentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        imgNext.setVisibility((currentIndex == userItemList.size() - 1 && !canLoadMoreUser())
                ? View.INVISIBLE : View.VISIBLE);
    }

    private void loadMoreUser() {
        isLoadingMoreUser = true;
        showLoading();
        profileDetailPresenter.loadMoreUser(nextPage, typeSearch);
    }

    private boolean canLoadMoreUser() {
        return (!nextPage.isEmpty() && !nextPage.equals("0")) ? true : false;
    }

    public ProfileDetailPresenter getProfileDetailPresenter() {
        return profileDetailPresenter;
    }
}


