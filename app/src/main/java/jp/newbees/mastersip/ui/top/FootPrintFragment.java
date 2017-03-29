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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.FootprintAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.FootprintItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.FootprintPresenter;
import jp.newbees.mastersip.ui.BaseCallFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemFragment;

/**
 * Created by thangit14 on 12/22/16.
 */

public class FootPrintFragment extends BaseCallFragment implements
        RadioGroup.OnCheckedChangeListener,
        FootprintPresenter.FootprintPresenterView,
        FootprintAdapter.FootprintViewHolder.OnFootprintClickListener {

    @BindView(R.id.rdo_footprint_viewed_by_other)
    RadioButton rdoFootprintViewedByOther;
    @BindView(R.id.rdo_footprint_viewed_by_me)
    RadioButton rdoFootprintViewedByMe;
    @BindView(R.id.rdo_footprint_group)
    RadioGroup rdoFootprintGroup;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.rcv_footprint)
    RecyclerView rcvFootprint;

    Unbinder unbinder;
    @BindView(R.id.txt_footprint_description)
    HiraginoTextView txtFootprintDescription;
    private FootprintAdapter adapterFootprint;

    private FootprintPresenter presenter;
    private String descriptionTotal;
    private SwipeRefreshLayout swipeContainer;
    private int currentCheckId;

    @Override
    protected int layoutId() {
        return R.layout.foot_print_fragment;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        super.init(rootView, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        this.imgBack.setVisibility(View.INVISIBLE);
        this.rdoFootprintGroup.setOnCheckedChangeListener(this);
        this.adapterFootprint = new FootprintAdapter(getContext(), new ArrayList<FootprintItem>());
        this.rcvFootprint.setAdapter(adapterFootprint);
        this.setFragmentTitle(getString(R.string.footprint));
        this.presenter = new FootprintPresenter(getContext(), this);
        this.rdoFootprintViewedByOther.setChecked(true);
        this.adapterFootprint.setOnItemClickListener(this);
        this.initRefreshView(rootView);
    }

    private void initRefreshView(View rootView) {
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleChangeListFootprint(currentCheckId);
            }
        });

    }

    public static Fragment newInstance() {
        Fragment fragment = new FootPrintFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
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
        this.handleChangeListFootprint(checkedId);
    }

    private void handleChangeListFootprint(int checkedId) {
        if (checkedId == rdoFootprintViewedByOther.getId()) {
            showLoading();
            descriptionTotal = getString(R.string.description_viewed_by_other);
            presenter.getListFootprintViewedByOther();
        } else {
            showLoading();
            descriptionTotal = getString(R.string.description_viewed_by_me);
            presenter.getListFootprintViewedByMe();
        }
    }

    private void updateTextColorSegment(int checkedId) {
        if (checkedId == rdoFootprintViewedByOther.getId()) {
            rdoFootprintViewedByOther.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoFootprintViewedByMe.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            rdoFootprintViewedByMe.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoFootprintViewedByOther.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    private void gotoProfileDetails(UserItem userItem) {
        ProfileDetailItemFragment fragment = ProfileDetailItemFragment.newInstance(userItem, false);
        this.txtActionBarTitle.setText(userItem.getUsername());
        showFragmentContent(fragment, ProfileDetailItemFragment.class.getName());
    }

    @Override
    public void didLoadListFootprint(ArrayList<FootprintItem> data, int totalFootprint) {
        disMissLoading();
        swipeContainer.setRefreshing(false);
        adapterFootprint.clearData();
        adapterFootprint.setData(data);
        adapterFootprint.notifyDataSetChanged();
        updateText(totalFootprint);
    }

    private void updateText(int total) {
        String description = String.format(descriptionTotal, total);
        this.txtFootprintDescription.setText(Html.fromHtml(description));
    }

    @Override
    public void didLoadDataError(int errorCode, String errorMessage) {
        swipeContainer.setRefreshing(false);
        disMissLoading();
        this.showMessageDialog(errorMessage);
    }

    @Override
    public void onChatClickListener(UserItem userItem) {
        super.requestChatClick(userItem);
    }

    @Override
    public void onVideoClickListener(UserItem userItem) {
        super.requestVideoCallClick(userItem);
    }

    @Override
    public void onVoiceClickListener(UserItem userItem) {
        super.requestVoiceCallClick(userItem);
    }

    @Override
    public void onProfileClickListener(UserItem userItem) {
        super.requestGotoProfile(userItem);
    }
}
