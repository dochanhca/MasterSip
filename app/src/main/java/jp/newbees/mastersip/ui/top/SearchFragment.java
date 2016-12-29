package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.customviews.SegmentedGroup;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.SearchPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.filter.FilterFragment;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.Mockup;

/**
 * Created by vietbq on 12/6/16.
 */

public class SearchFragment extends BaseFragment implements SearchPresenter.SearchView {
    @BindView(R.id.recycler_user)
    RecyclerView recyclerUser;
    private SearchPresenter presenter;
    private static final int MODE_FOUR_COLUMN = 4;
    private static final int MODE_TWO_COLUMN = 2;
    private static final int MODE_LIST = 0;
    private int currentFilterMode = MODE_FOUR_COLUMN;

    private AdapterSearchUserModeFour adapterSearchUserModeFour;
    private AdapterSearchUserModeTwo adapterSearchUserModeTwo;
    private AdapterSearUserModeList adapterSearUserModeList;

        private ArrayList<UserItem> userItems = Mockup.getUserItems();
//    private ArrayList<UserItem> userItems = new ArrayList<>();

    @BindView(R.id.txt_search)
    HiraginoTextView txtSearch;
    @BindView(R.id.txt_phone)
    HiraginoTextView txtPhone;
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

    private HashMap<Integer, Integer> FILTER_MODE_INDEXS;
    private android.support.v7.widget.RecyclerView.ItemDecoration mItemDecoration;

    @Override
    protected int layoutId() {
        return R.layout.search_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        presenter = new SearchPresenter(getContext(), this);
        ButterKnife.bind(this, mRoot);
        btnFilterCallWaiting.setChecked(true);

        initFilterMode();
        presenter.filterUser();
//        changeUIContent(currentFilterMode);
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.img_filter, R.id.header_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_filter:
                changeMode();
                break;
            case R.id.header_search:
                showFilterFragment();
                break;
        }
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
        }
    }

    private void setupListViewWithModeList() {
        if (adapterSearUserModeList == null) {
            adapterSearUserModeList = new AdapterSearUserModeList(getContext(), userItems);
        } else {
            adapterSearUserModeList.addAll(userItems);
        }
        recyclerUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerUser.setAdapter(adapterSearUserModeList);
        mItemDecoration = null;
    }

    private void setupListViewWithModeTwo() {
        if (adapterSearchUserModeTwo == null) {
            adapterSearchUserModeTwo = new AdapterSearchUserModeTwo(getContext(), userItems);
        } else {
            adapterSearchUserModeTwo.addAll(userItems);
        }
        recyclerUser.setLayoutManager(new GridLayoutManager(getActivity(), currentFilterMode));
        mItemDecoration = new GridSpacingItemDecoration(currentFilterMode, getResources().getDimensionPixelSize(R.dimen.item_offset_mode_two), true);
        recyclerUser.addItemDecoration(mItemDecoration);
        recyclerUser.setAdapter(adapterSearchUserModeTwo);

    }

    private void setupListViewWithModeFour() {
        if (adapterSearchUserModeFour == null) {
            adapterSearchUserModeFour = new AdapterSearchUserModeFour(getContext(), userItems);
        } else {
            adapterSearchUserModeFour.addAll(userItems);
        }
        recyclerUser.setLayoutManager(new GridLayoutManager(getActivity(), currentFilterMode));

        mItemDecoration = new GridSpacingItemDecoration(currentFilterMode, getResources().getDimensionPixelSize(R.dimen.item_offset_mode_four), true);
        recyclerUser.addItemDecoration(mItemDecoration);
        recyclerUser.setAdapter(adapterSearchUserModeFour);

    }

    private void changeFilterImage() {
        StringBuilder uri = new StringBuilder("@drawable/ic_");
        uri.append(currentFilterMode + "");
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

    private void showFilterFragment() {
        FilterFragment filterFragment = FilterFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_search_container, filterFragment,
                FilterFragment.class.getName()).commit();
    }

    @Override
    public void didFilterUser(ArrayList<UserItem> userItems) {
        Logger.e("SearchFragment", "userItems " + userItems.size());
        this.userItems = userItems;
        changeUIContent(currentFilterMode);

    }

    @Override
    public void didFilterUserError(int errorCode, String errorMessage) {

    }
}
