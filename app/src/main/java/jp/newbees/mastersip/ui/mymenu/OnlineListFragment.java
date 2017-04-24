package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.OnlineListAdapter;
import jp.newbees.mastersip.customviews.EndlessRecyclerViewScrollListener;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.mymenu.OnlineListPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.profile.SettingOnlineFragment;

/**
 * Created by ducpv on 2/14/17.
 */

public class OnlineListFragment extends BaseFragment implements OnlineListAdapter.OnItemClickListener, OnlineListPresenter.OnlineListView {

    @BindView(R.id.rcv_online_list)
    RecyclerView rcvOnlineList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;

    private OnlineListPresenter onlineListPresenter;
    private OnlineListAdapter adapter;

    public static BaseFragment newInstance() {
        BaseFragment onlineListFragment = new OnlineListFragment();
        Bundle args = new Bundle();
        onlineListFragment.setArguments(args);
        return onlineListFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_online_list;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        onlineListPresenter = new OnlineListPresenter(getActivity(), this);
        unbinder = ButterKnife.bind(this, mRoot);
        setFragmentTitle(getString(R.string.list_online_notify));

        initOnlineList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLoading();
                onlineListPresenter.loadData();
            }
        });
        showLoading();
        onlineListPresenter.loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        onlineListPresenter.registerEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        onlineListPresenter.unRegisterEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onChangeOnlineSettingClick(int position) {
        UserItem userItem = adapter.getData().get(position);
        BaseFragment settingOnlineFragment = SettingOnlineFragment.newInstance(userItem, false);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        settingOnlineFragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_my_menu_container, settingOnlineFragment).commit();
    }

    @Override
    public void didLoadOnlineList(List<UserItem> userItems) {
        disMissLoading();
        adapter.clearData();
        adapter.addAll(userItems);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didLoadMoreOnlineList(List<UserItem> userItems) {
        disMissLoading();
        adapter.addAll(userItems);
    }

    @Override
    public void didLoadOnlineListError(int errorCode, String errorMessage) {
        disMissLoading();
        swipeRefreshLayout.setRefreshing(false);
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    private void initOnlineList() {
        adapter = new OnlineListAdapter(getActivity().getApplicationContext(), new ArrayList<UserItem>());
        adapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rcvOnlineList.setAdapter(adapter);
        rcvOnlineList.setLayoutManager(layoutManager);

        rcvOnlineList.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((LinearLayoutManager) rcvOnlineList.getLayoutManager()) {
            boolean needLoadMore = false;
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                needLoadMore = dy > 0 ? true : false;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (needLoadMore) {
                    showLoading();
                    onlineListPresenter.loadMoreData();
                }
            }
        });
    }
}
