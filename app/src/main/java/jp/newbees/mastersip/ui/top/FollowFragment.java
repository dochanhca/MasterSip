package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.FollowAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.FollowItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.FollowPresenter;
import jp.newbees.mastersip.ui.BaseCallFragment;

/**
 * Created by vietbq on 1/23/17.
 */

public class FollowFragment extends BaseCallFragment implements RadioGroup.OnCheckedChangeListener,
        FollowPresenter.FollowPresenterView,
        FollowAdapter.FollowViewHolder.OnFollowItemClickListener {

    @BindView(R.id.rdo_follow_group)
    RadioGroup rdoFollowGroup;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_action_bar_title)
    HiraginoTextView txtActionBarTitle;
    @BindView(R.id.rdo_followers)
    RadioButton rdoFollowers;
    @BindView(R.id.rdo_following)
    RadioButton rdoFollowing;
    @BindView(R.id.txt_follow_description)
    HiraginoTextView txtFollowDescription;
    @BindView(R.id.rcv_follow)
    RecyclerView rcvFollow;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;
    private FollowAdapter adapterFollow;
    private FollowPresenter presenter;
    private String descriptionTotal;
    private int currentCheckId;

    public static Fragment newInstance() {
        Fragment fragment = new FollowFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_flow;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, rootView);
        this.imgBack.setVisibility(View.INVISIBLE);
        this.rdoFollowGroup.setOnCheckedChangeListener(this);
        this.adapterFollow = new FollowAdapter(getActivity().getApplicationContext(), new FollowItem());
        this.rcvFollow.setAdapter(adapterFollow);
        this.setFragmentTitle(getString(R.string.follower));
        this.presenter = new FollowPresenter(getContext(), this);
        this.rdoFollowers.setChecked(true);
        this.adapterFollow.setOnItemFollowClickListener(this);
        this.initRefreshView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        this.updateTextColorSegment(checkedId);
        this.currentCheckId = checkedId;
        this.handleChangeListFollow(checkedId);
    }

    @Override
    public void didLoadListFollow(FollowItem data) {
        disMissLoading();
        swipeRefreshLayout.setRefreshing(false);
        adapterFollow.clearData();
        adapterFollow.setData(data);
        adapterFollow.notifyDataSetChanged();
        updateText(data.getTotal());
    }

    @Override
    public void didLoadDataError(int errorCode, String errorMessage) {
        swipeRefreshLayout.setRefreshing(false);
        disMissLoading();
        this.showMessageDialog(errorMessage);
    }

    @Override
    public void onChatClickListener(UserItem userItem) {
        super.chatWithUser(userItem);
    }

    @Override
    public void onVideoClickListener(UserItem userItem) {
        super.callVideo(userItem, false);
    }

    @Override
    public void onVoiceClickListener(UserItem userItem) {
        super.callVoice(userItem, false);
    }

    @Override
    public void onProfileClickListener(UserItem userItem) {
        super.gotoProfileDetail(userItem);
    }

    private void updateTextColorSegment(int checkedId) {
        if (checkedId == rdoFollowers.getId()) {
            rdoFollowers.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoFollowing.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            rdoFollowing.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoFollowers.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    private void initRefreshView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleChangeListFollow(currentCheckId);
            }
        });
    }

    private void handleChangeListFollow(int checkedId) {
        if (checkedId == rdoFollowers.getId()) {
            showLoading();
            descriptionTotal = getString(R.string.description_followers);
            presenter.getListFollowers();
        } else {
            showLoading();
            descriptionTotal = getString(R.string.description_followings);
            presenter.getListFollowing();
        }
    }

    private void updateText(int totalFollow) {
        String description = String.format(descriptionTotal, totalFollow);
        this.txtFollowDescription.setText(Html.fromHtml(description));
    }

    public final void setLeftTabChecked() {
        rdoFollowers.setChecked(true);
    }

    public final void reloadBadge() {
        showLoading();
        presenter.getListFollowers();
    }
}
