package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.IncomingWaitingFragment;
import jp.newbees.mastersip.ui.call.VideoCallFragment;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseActivity implements
        BaseHandleIncomingCallPresenter.IncomingCallView {

    protected static final String CALLER = "CALLER";
    protected static final String CALL_ID = "CALL_ID";

    private BaseHandleIncomingCallPresenter presenter;

    private UserItem caller;
    private String callId;

    private IncomingWaitingFragment incomingWaitingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        this.presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.checkFlashCall();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_in_coming_voice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    protected abstract int getAcceptCallImage();

    protected abstract String getTitleCall();

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        caller = getIntent().getExtras().getParcelable(CALLER);
        callId = getIntent().getExtras().getString(CALL_ID);

        showIncomingWaitingFragment(caller, callId, getAcceptCallImage(), getTitleCall(), getCallType());
    }

    @Override
    public void onCoinChanged(int coin) {
        incomingWaitingFragment.onCoinChanged(coin);
    }

    private void showIncomingWaitingFragment(UserItem caller, String callId,
                                             int acceptCallImage, String titleCall, int callType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        incomingWaitingFragment = IncomingWaitingFragment.newInstance(caller, callId, acceptCallImage, titleCall, callType);
        transaction.add(R.id.fragment_container, incomingWaitingFragment,
                IncomingWaitingFragment.class.getName()).commit();
    }

    protected void showVideoCallFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = VideoCallFragment.newInstance();
        transaction.replace(R.id.fragment_container, fragment,
                VideoCallFragment.class.getName()).commit();
    }

    protected void showCallingViewOnVoiceCall() {
        incomingWaitingFragment.showCallingViewOnVoiceCall();

    }

    protected void countingCallDuration() {
        incomingWaitingFragment.countingCallDuration();
    }

    public final void rejectCall(String caller, int callType, String calId) {
        this.presenter.rejectCall(caller, callType, calId);
    }

    public final void acceptCall(String calId) {
        this.presenter.acceptCall(calId);
    }

    public final void endCall(String caller, int callType, String calId) {
        this.presenter.endCall(caller, callType, calId);
    }

    protected final void updateSpeaker() {
        enableSpeaker(incomingWaitingFragment.isEnableSpeaker());
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    protected final void startVideoCall() {
        presenter.startVideoCall();
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onFlashedCall() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    @Override
    public void onCallConnected() {
        // override this if need listener callback
    }

    @Override
    public void onStreamingConnected() {
        // override this if need listener callback
    }

    protected abstract int getCallType();

}
