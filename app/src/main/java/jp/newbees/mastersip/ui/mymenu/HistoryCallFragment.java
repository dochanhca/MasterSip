package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
import android.support.annotation.IdRes;
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
import jp.newbees.mastersip.adapter.HistoryCallAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.CallLogItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.HistoryCallPresenter;
import jp.newbees.mastersip.ui.BaseCallFragment;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 4/18/17.
 */

public class HistoryCallFragment extends BaseCallFragment implements RadioGroup.OnCheckedChangeListener, HistoryCallAdapter.HistoryCallViewHolder.OnHistoryCallClickListener, HistoryCallPresenter.HistoryCallPresenterView {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_action_bar_title)
    HiraginoTextView txtActionBarTitle;
    @BindView(R.id.rdo_incoming_call)
    RadioButton rdoIncomingCall;
    @BindView(R.id.rdo_outgoing_call)
    RadioButton rdoOutgoingCall;
    @BindView(R.id.rdo_history_group)
    RadioGroup rdoHistoryGroup;
    @BindView(R.id.txt_history_call_description)
    HiraginoTextView txtHistoryCallDescription;
    @BindView(R.id.rcv_history_call)
    RecyclerView rcvHistoryCall;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    Unbinder unbinder;

    private int currentCheckId;
    private HistoryCallAdapter adapterHistoryCall;
    private HistoryCallPresenter presenter;
    private String descriptionTotal;

    @Override
    protected int layoutId() {
        return R.layout.fragment_history_call;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, rootView);
        this.imgBack.setVisibility(View.VISIBLE);
        this.rdoHistoryGroup.setOnCheckedChangeListener(this);
        this.adapterHistoryCall = new HistoryCallAdapter(getActivity().getApplicationContext(), new ArrayList<CallLogItem>());
        this.rcvHistoryCall.setAdapter(adapterHistoryCall);
        this.setFragmentTitle(getString(R.string.history_call));
        this.presenter = new HistoryCallPresenter(getContext(), this);
        this.rdoIncomingCall.setChecked(true);
        this.adapterHistoryCall.setOnItemClickListener(this);
        this.initRefreshView(rootView);
    }

    private void initRefreshView(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleChangeCallLogs(currentCheckId);
            }
        });
    }

    public static BaseFragment newInstance() {
        BaseFragment historyCallFragment = new HistoryCallFragment();
        return historyCallFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        this.updateTextColorSegment(checkedId);
        this.currentCheckId = checkedId;
        this.handleChangeCallLogs(checkedId);
    }

    private void handleChangeCallLogs(int checkedId) {
        if (checkedId == rdoIncomingCall.getId()) {
            showLoading();
            descriptionTotal = getString(R.string.description_incoming_call_logs);
            presenter.getIncomingCallLogs();
        } else {
            showLoading();
            descriptionTotal = getString(R.string.description_outgoing_call_logs);
            presenter.getOutgoingCallLogs();
        }
    }

    private void updateTextColorSegment(int checkedId) {
        if (checkedId == rdoIncomingCall.getId()) {
            rdoIncomingCall.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoOutgoingCall.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            rdoOutgoingCall.setTextColor(getContext().getResources().getColor(R.color.white));
            rdoIncomingCall.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }
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

    @Override
    public void didLoadCallLogs(ArrayList<CallLogItem> data, int totalCallLog) {
        disMissLoading();
        swipeRefreshLayout.setRefreshing(false);
        adapterHistoryCall.clearData();
        adapterHistoryCall.setData(data);
        adapterHistoryCall.notifyDataSetChanged();
        updateText(totalCallLog);
    }

    private void updateText(int totalCallLog) {
        String description = String.format(descriptionTotal, totalCallLog);
        this.txtHistoryCallDescription.setText(Html.fromHtml(description));
    }

    @Override
    public void didLoadDataError(int errorCode, String errorMessage) {
        swipeRefreshLayout.setRefreshing(false);
        disMissLoading();
        this.showMessageDialog(errorMessage);
    }
}
