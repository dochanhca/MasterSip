package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.OutgoingWaitingFragment;
import jp.newbees.mastersip.ui.call.VideoCallFragment;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseActivity implements BaseHandleOutgoingCallPresenter.OutgoingCallView {
    protected static final String CALLEE = "CALLEE";

    private BaseHandleOutgoingCallPresenter presenter;
    private UserItem callee;
    private int callType;

    OutgoingWaitingFragment outgoingWaitingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        this.callType = getCallType();
        this.presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    protected abstract String getTextTitle();

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
        outgoingWaitingFragment.onCoinChange(coin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    private void showOutgoingWaitingFragment(UserItem callee, String titleCall, int callType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        outgoingWaitingFragment = OutgoingWaitingFragment.newInstance(callee, titleCall, callType);
        transaction.add(R.id.fragment_container, outgoingWaitingFragment,
                OutgoingWaitingFragment.class.getName()).commit();
    }

    protected void showVideoCallFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = VideoCallFragment.newInstance();
        transaction.replace(R.id.fragment_container, fragment,
                VideoCallFragment.class.getName()).commit();
    }

    protected void switchCamera() {
        presenter.switchCamera();
    }

    protected void userFrontCamera() {
        presenter.switchCamera();
    }

    public final void endCall() {
        this.presenter.endCall(callee, callType);
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    public final void changeCamera() {

    }

    public final void enableCamera(boolean enableCamera) {

    }

    protected void countingCallDuration() {
        outgoingWaitingFragment.countingCallDuration();
    }

    protected void updateViewWhenVoiceConnected() {
        outgoingWaitingFragment.updateViewWhenVoiceConnected();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    protected abstract int getCallType();
}
