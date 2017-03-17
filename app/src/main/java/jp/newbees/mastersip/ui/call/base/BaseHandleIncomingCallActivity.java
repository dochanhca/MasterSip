package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.call.IncomingWaitingFragment;
import jp.newbees.mastersip.ui.call.VideoCallFragment;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseHandleCallActivity implements
        BaseHandleIncomingCallPresenter.IncomingCallView {

    protected static final String CALLER = "CALLER";
    protected static final String CALL_ID = "CALL_ID";

    private BaseHandleIncomingCallPresenter presenter;

    private UserItem caller;
    private String callId;

    private IncomingWaitingFragment incomingWaitingFragment;
    private VideoCallFragment videoCallFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        setPresenter(presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.checkFlashCall();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_in_coming_voice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public UserItem getCurrentUser() {
        return caller;
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
        if (caller.getGender() == UserItem.FEMALE) {
            if (incomingWaitingFragment == null) {
                videoCallFragment.onCoinChanged(coin);
            } else {
                incomingWaitingFragment.onCoinChanged(coin);
            }
        }
    }

    private void showIncomingWaitingFragment(UserItem caller, String callId,
                                             int acceptCallImage, String titleCall, int callType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        incomingWaitingFragment = IncomingWaitingFragment.newInstance(caller, callId, acceptCallImage, titleCall, callType);
        transaction.add(R.id.fragment_container, incomingWaitingFragment,
                IncomingWaitingFragment.class.getName()).commit();
    }

    protected void showVideoCallFragment() {
        incomingWaitingFragment = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        videoCallFragment = VideoCallFragment.newInstance(getCurrentUser(), getCallType());
        transaction.replace(R.id.fragment_container, videoCallFragment,
                VideoCallFragment.class.getName()).commit();
    }

    protected void showCallingViewOnVoiceCall() {
        incomingWaitingFragment.showCallingViewOnVoiceCall();

    }

    protected void countingCallDuration() {
        incomingWaitingFragment.countingCallDuration();
    }

    protected final void updateSpeaker() {
        enableSpeaker(incomingWaitingFragment.isEnableSpeaker());
    }

    protected final void startVideoCall() {
        presenter.startVideoCall();
    }

    public UserItem getCaller() {
        return caller;
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
    public void onCallConnected() {
        // override this if need listener callback
    }

    @Override
    public void onStreamingConnected() {
        // override this if need listener callback
    }

}
