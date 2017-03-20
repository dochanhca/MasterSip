package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.ui.call.OutgoingWaitingFragment;
import jp.newbees.mastersip.ui.call.VideoCallFragment;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseHandleCallActivity {
    protected static final String CALLEE = "CALLEE";

    private BaseHandleOutgoingCallPresenter presenter;
    private UserItem callee;

    private OutgoingWaitingFragment outgoingWaitingFragment;
    private VideoCallFragment videoCallFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        setPresenter(presenter);

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    protected abstract String getTextTitle();

    @Override
    public UserItem getCurrentUser() {
        return callee;
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        callee = getIntent().getExtras().getParcelable(CALLEE);
        showOutgoingWaitingFragment(callee, getTextTitle(), getCallType());
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_out_going_voice;
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onCoinChanged(int coin) {
        if (callee.getGender() == UserItem.MALE) {
            if (outgoingWaitingFragment == null) {
                videoCallFragment.onCoinChanged(coin);
            } else {
                outgoingWaitingFragment.onCoinChange(coin);
            }
        }
    }

    protected void showVideoCallFragment() {
        outgoingWaitingFragment = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        videoCallFragment = VideoCallFragment.newInstance(getCurrentUser(), getCallType());
        transaction.replace(R.id.fragment_container, videoCallFragment,
                VideoCallFragment.class.getName()).commit();
    }

    public UserItem getCallee() {
        return callee;
    }

    private void showOutgoingWaitingFragment(UserItem callee, String titleCall, int callType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        outgoingWaitingFragment = OutgoingWaitingFragment.newInstance(callee, titleCall, callType);
        transaction.add(R.id.fragment_container, outgoingWaitingFragment,
                OutgoingWaitingFragment.class.getName()).commit();
    }

    protected void countingCallDuration() {
        outgoingWaitingFragment.countingCallDuration();
    }

    protected void updateViewWhenVoiceConnected() {
        outgoingWaitingFragment.updateViewWhenVoiceConnected();
    }
}
