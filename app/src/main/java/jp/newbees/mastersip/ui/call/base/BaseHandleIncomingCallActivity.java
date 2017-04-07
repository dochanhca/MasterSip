package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.call.IncomingWaitingFragment;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseHandleCallActivity implements
        BaseHandleIncomingCallPresenter.IncomingCallView {
    private BaseHandleIncomingCallPresenter presenter;

    protected abstract int getAcceptCallImage();
    protected abstract String getTitleCall();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        setPresenter(presenter);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        super.initVariables(savedInstanceState);

        showIncomingWaitingFragment(getCompetitor(), getCallId(), getAcceptCallImage(), getTitleCall(), getCallType());
    }

    private void showIncomingWaitingFragment(UserItem caller, String callId,
                                             int acceptCallImage, String titleCall, int callType) {
        showWaitingFragment(IncomingWaitingFragment.newInstance(caller, callId, acceptCallImage, titleCall, callType));
    }

    protected void showVideoCallFragment() {
        showVideoCallFragment(getCompetitor(),getCallId(),getCallType(),
                false,false);
    }

    protected final void updateSpeakerInWaitingFragment() {
        if (getVisibleFragment() instanceof IncomingWaitingFragment) {
            enableSpeaker(((IncomingWaitingFragment) getVisibleFragment()).isEnableSpeaker());
        }
    }

    protected final void startIncomingVideoCall() {
        presenter.startIncomingVideoCall();
    }

    @Override
    public void onCallEnd() {
        if (MyLifecycleHandler.getNumberOfActivity() == 1) {
            Logger.e(TAG,"we have only calling activity, stop service and destroy app");
            LinphoneService.stopLinphone(this);
        }
        super.onCallEnd();
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
