package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
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
import jp.newbees.mastersip.adapter.BlockListAdapter;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.mymenu.BlockListPresenter;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 5/25/17.
 */

public class BlockListFragment extends BaseFragment implements BlockListPresenter.BlockListView, BlockListAdapter.OnItemClickListener {

    @BindView(R.id.rcv_block_list)
    RecyclerView rcvBlockList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;

    private BlockListPresenter blockListPresenter;
    private BlockListAdapter adapter;
    private int position;

    public static BaseFragment newInstance() {
        BaseFragment blockListFragment = new BlockListFragment();
        Bundle args = new Bundle();
        blockListFragment.setArguments(args);
        return blockListFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_block_list;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentTitle(getString(R.string.block_list));
        initBlockList();
        restoreNavigationBarState();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLoading();
                blockListPresenter.loadData();
            }
        });
        showLoading();
        blockListPresenter = new BlockListPresenter(getActivity(), this);
        blockListPresenter.loadData();
    }

    @Override
    public void didLoadBlockList(List<UserItem> userItems) {
        disMissLoading();
        adapter.clearData();
        adapter.addAll(userItems);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didLoadBlockListError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didSetUserBlocked(boolean isBlocked) {
        disMissLoading();
        adapter.getData().get(position).setBlocked(isBlocked);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void didSetUserBlockedError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void onBlockUserChange(int position, boolean isBLocked) {
        showLoading();
        this.position = position;
        UserItem currentUser = adapter.getData().get(position);
        blockListPresenter.setUserBlocked(currentUser.getUserId(), isBLocked);
    }

    private void initBlockList() {
        adapter = new BlockListAdapter(getActivity().getApplicationContext(), new ArrayList<UserItem>());
        adapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rcvBlockList.setAdapter(adapter);
        rcvBlockList.setLayoutManager(layoutManager);
    }
}
