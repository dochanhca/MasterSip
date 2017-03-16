package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterSearchUserModeFour;
import jp.newbees.mastersip.adapter.AdapterSearchUserModeList;
import jp.newbees.mastersip.adapter.AdapterSearchUserModeTwo;
import jp.newbees.mastersip.customviews.EndlessRecyclerViewScrollListener;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.customviews.SegmentedGroup;
import jp.newbees.mastersip.event.FilterUserEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.FilterUserTask;
import jp.newbees.mastersip.presenter.top.FilterUserPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.filter.FilterFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailFragment;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.Mockup;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by vietbq on 12/6/16.
 */

public class SearchFragment extends BaseFragment implements FilterUserPresenter.SearchView,
        AdapterSearchUserModeList.OnItemClickListener, AdapterSearchUserModeFour.OnItemClickListener,
        AdapterSearchUserModeTwo.OnItemClickListener {

    @BindView(R.id.recycler_user)
    RecyclerView recyclerUser;
    @BindView(R.id.txt_search)
    HiraginoTextView txtSearch;
    @BindView(R.id.btn_setting_call)
    HiraginoTextView btnSettingCall;
    @BindView(R.id.header_search)
    RelativeLayout headerSearch;
    @BindView(R.id.btn_filter_call_waiting)
    RadioButton btnFilterCallWaiting;
    @BindView(R.id.btn_filter_new)
    RadioButton btnFilterNew;
    @BindView(R.id.btn_filter_all)
    RadioButton btnFilterAll;
    @BindView(R.id.segmented_filter)
    SegmentedGroup segmentedFilter;
    @BindView(R.id.img_filter)
    ImageView imgFilter;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.filter)
    ViewGroup filter;

    private int currentTypeSearch = Constant.API.AVAILABLE_CALL;

    private FilterUserPresenter presenter;
    private static final int MODE_FOUR_COLUMN = 4;
    private static final int MODE_TWO_COLUMN = 2;
    private static final int MODE_LIST = 1;
    private int currentFilterMode = MODE_FOUR_COLUMN;

    private AdapterSearchUserModeFour adapterSearchUserModeFour;
    private AdapterSearchUserModeTwo adapterSearchUserModeTwo;
    private AdapterSearchUserModeList adapterSearUserModeList;

    private GridLayoutManager layoutManager;

    private ArrayList<UserItem> userItems = Mockup.getUserItems();
    private String nextPage;

    private HashMap<Integer, Integer> FILTER_MODE_INDEXS;
    private RecyclerView.ItemDecoration mItemDecoration;
    private EndlessRecyclerViewScrollListener listener;

    private Animation slideDown;
    private Animation slideUp;

    private boolean isShowFilterAndNavigationBar = true;

    private RadioGroup.OnCheckedChangeListener mOnSegmentedFilterChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.btn_filter_call_waiting:
                    currentTypeSearch = Constant.API.AVAILABLE_CALL;
                    break;
                case R.id.btn_filter_new:
                    currentTypeSearch = Constant.API.NEW_USER;
                    break;
                case R.id.btn_filter_all:
                    currentTypeSearch = Constant.API.ALL_USER;
                    break;
                default:
                    break;
            }
            showLoading();
            presenter.filterUser(currentTypeSearch);
        }
    };

    /**
     * create newInstance of Fragment
     *
     * @return
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.search_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_to_show);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_to_hide);

        presenter = new FilterUserPresenter(getContext(), this);
        ButterKnife.bind(this, mRoot);
        btnFilterCallWaiting.setChecked(true);

        initFilterMode();
        presenter.filterUser(currentTypeSearch);
        recyclerUser.setMinimumHeight(Utils.getScreenHeight(getActivity().getApplicationContext()));

        swipeRefreshLayout.setProgressViewOffset(false, (int) getResources().getDimension(R.dimen.header_filter_height),
                2 * (int) getResources().getDimension(R.dimen.header_filter_height));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.filterUser(currentTypeSearch);
                listener.resetState();
            }
        });

        segmentedFilter.setOnCheckedChangeListener(mOnSegmentedFilterChangeListener);
    }

    @OnClick({R.id.img_filter, R.id.header_search, R.id.btn_setting_call, R.id.txt_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_filter:
                changeMode();
                break;
            case R.id.txt_search:
                showFilterFragment();
                break;
            case R.id.btn_setting_call:
                showSettingCallFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * on filter user event
     *
     * @param event Filter's settings changed
     */
    @Subscribe(sticky = true)
    public void onFilterUserEvent(FilterUserEvent event) {
        Logger.e(TAG, "onFilterUserEvent receive");

        showLoading();
        presenter.filterUser(currentTypeSearch);
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void addScrollToLoadMore() {
        listener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);

                if (dy > 0 && isShowFilterAndNavigationBar) {
                    isShowFilterAndNavigationBar = false;
                    hideFilterAndNavigationBar();
                } else if (dy < 0 && !isShowFilterAndNavigationBar) {
                    isShowFilterAndNavigationBar = true;
                    showFilterAndNavigationBar();
                }
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (presenter.canLoadMoreUser() && totalItemsCount == 20) {
                    showLoading();
                    presenter.loadMoreUser(currentTypeSearch);
                }
            }

            private void hideFilterAndNavigationBar() {
                clearViewAnimation(filter, slideUp, View.GONE);
                filter.startAnimation(slideUp);
                ((TopActivity) getActivity()).hideNavigation();
            }
        };

        recyclerUser.addOnScrollListener(listener);
    }

    private void changeMode() {
        setCurrentToNextFilterMode();
        changeFilterImage();
        changeUIContent(currentFilterMode);
    }

    private void changeUIContent(int currentFilterMode) {
        if (mItemDecoration != null) {
            recyclerUser.removeItemDecoration(mItemDecoration);
        }
        layoutManager = new GridLayoutManager(getActivity(), currentFilterMode);
        addScrollToLoadMore();

        switch (currentFilterMode) {
            case MODE_FOUR_COLUMN:
                setupListViewWithModeFour();
                break;
            case MODE_TWO_COLUMN:
                setupListViewWithModeTwo();
                break;
            case MODE_LIST:
                setupListViewWithModeList();
                break;
            default:
                setupListViewWithModeFour();
                break;
        }
    }

    private void setupListViewWithModeList() {
        if (adapterSearUserModeList == null) {
            adapterSearUserModeList = new AdapterSearchUserModeList(getContext(), userItems);
            adapterSearUserModeList.setOnItemClickListener(this);
        } else {
            adapterSearUserModeList.addAll(userItems);
            adapterSearUserModeList.notifyDataSetChanged();
        }
        recyclerUser.setLayoutManager(layoutManager);
        recyclerUser.setAdapter(adapterSearUserModeList);
        mItemDecoration = null;
    }

    private void setupListViewWithModeTwo() {
        if (adapterSearchUserModeTwo == null) {
            adapterSearchUserModeTwo = new AdapterSearchUserModeTwo(getContext(), userItems);
            adapterSearchUserModeTwo.setOnItemClickListener(this);
        } else {
            adapterSearchUserModeTwo.addAll(userItems);
            adapterSearchUserModeTwo.notifyDataSetChanged();
        }
        recyclerUser.setLayoutManager(layoutManager);
        mItemDecoration = new GridSpacingItemDecoration(currentFilterMode, getResources().getDimensionPixelSize(R.dimen.item_offset_mode_two), true);
        recyclerUser.addItemDecoration(mItemDecoration);
        recyclerUser.setAdapter(adapterSearchUserModeTwo);
    }

    private void setupListViewWithModeFour() {
        if (adapterSearchUserModeFour == null) {
            adapterSearchUserModeFour = new AdapterSearchUserModeFour(getContext(), userItems);
            adapterSearchUserModeFour.setOnItemClickListener(this);
        } else {
            adapterSearchUserModeFour.addAll(userItems);
            adapterSearchUserModeFour.notifyDataSetChanged();
        }
        recyclerUser.setLayoutManager(layoutManager);

        mItemDecoration = new GridSpacingItemDecoration(currentFilterMode, getResources().getDimensionPixelSize(R.dimen.item_offset_mode_four), true);
        recyclerUser.addItemDecoration(mItemDecoration);
        recyclerUser.setAdapter(adapterSearchUserModeFour);
    }

    private void changeFilterImage() {
        StringBuilder uri = new StringBuilder("@drawable/ic_");
        uri.append(Integer.toString(currentFilterMode));
        int imageResource = getResources().getIdentifier(uri.toString(), null, getActivity().getPackageName());
        imgFilter.setImageDrawable(getResources().getDrawable(imageResource));
    }

    private void setCurrentToNextFilterMode() {
        currentFilterMode = FILTER_MODE_INDEXS.get(currentFilterMode);
    }

    private void initFilterMode() {
        FILTER_MODE_INDEXS = new HashMap<>();
        FILTER_MODE_INDEXS.put(MODE_FOUR_COLUMN, MODE_TWO_COLUMN);
        FILTER_MODE_INDEXS.put(MODE_TWO_COLUMN, MODE_LIST);
        FILTER_MODE_INDEXS.put(MODE_LIST, MODE_FOUR_COLUMN);
    }

    /**
     * Add filter fragment to stack instead of replace
     */
    private void showFilterFragment() {
        showNavigationAndActionBar();
        FilterFragment filterFragment = FilterFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, filterFragment,
                FilterFragment.class.getName()).commit();
    }

    /**
     * Add ProfileDetail Fragment to stack instead of replace
     */
    private void showProfileDetailFragment(int position) {
        showNavigationAndActionBar();
        ProfileDetailFragment profileDetailFragment =
                ProfileDetailFragment.newInstance(userItems, position, nextPage, currentTypeSearch);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, profileDetailFragment,
                ProfileDetailFragment.class.getName()).commit();
    }

    /**
     * Add SettingCall Fragment to stack instead of replace
     */
    private void showSettingCallFragment() {
        showNavigationAndActionBar();
        SettingCallFragment settingFragment = SettingCallFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, settingFragment,
                SettingCallFragment.class.getName()).commit();
    }

    @Override
    public void onItemClick(UserItem userItem, int position) {
        showProfileDetailFragment(position);
    }

    @Override
    public void didFilterUser(Map<String, Object> data) {
        List<UserItem> users = (List<UserItem>) data.get(FilterUserTask.LIST_USER);
        nextPage = (String) data.get(FilterUserTask.NEXT_PAGE);

        Logger.e("SearchFragment", "userItems " + users.size());
        this.userItems.clear();
        this.userItems.addAll(users);
        changeUIContent(currentFilterMode);
        swipeRefreshLayout.setRefreshing(false);
        disMissLoading();
    }

    @Override
    public void didFilterUserError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(
                errorCode, errorMessage);
        swipeRefreshLayout.setRefreshing(false);
        disMissLoading();
    }

    @Override
    public void didLoadMoreUser(Map<String, Object> data) {
        List<UserItem> users = (List<UserItem>) data.get(FilterUserTask.LIST_USER);
        userItems.addAll(users);
        nextPage = (String) data.get(FilterUserTask.NEXT_PAGE);

        notifyListUserChanged();
        disMissLoading();
    }

    private void notifyListUserChanged() {
        switch (currentFilterMode) {
            case MODE_FOUR_COLUMN:
                adapterSearchUserModeFour.notifyDataSetChanged();
                break;
            case MODE_LIST:
                adapterSearUserModeList.notifyDataSetChanged();
                break;
            case MODE_TWO_COLUMN:
                adapterSearchUserModeTwo.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void showFilterAndNavigationBar() {
        clearViewAnimation(filter, slideUp, View.VISIBLE);
        filter.startAnimation(slideDown);
        ((TopActivity) getActivity()).showNavigation();
    }

    private void showNavigationAndActionBar() {
        if (!isShowFilterAndNavigationBar) {
            isShowFilterAndNavigationBar = true;
            showFilterAndNavigationBar();
        }
    }
}
