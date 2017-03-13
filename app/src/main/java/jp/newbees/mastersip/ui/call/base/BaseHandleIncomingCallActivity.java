package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.CallCenterIncomingActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseActivity implements
        BaseHandleIncomingCallPresenter.IncomingCallView {

    private BaseHandleIncomingCallPresenter presenter;

    private UserItem caller;
    private String callId;

    protected abstract int getCallType();

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

//    @Override
//    public void onRunOutOfCoin() {
//        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
//            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
//        }
//    }

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

    public final void rejectCall(String caller, int callType, String calId) {
        this.presenter.rejectCall(caller, callType, calId);
    }

    public final void acceptCall(String calId) {
        this.presenter.acceptCall(calId);
    }

    public final void endCall(String caller, int callType, String calId) {
        this.presenter.endCall(caller, callType, calId);
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    protected final UserItem getCaller() {
        if (caller == null) {
            caller = getIntent().getExtras().getParcelable(CallCenterIncomingActivity.CALLER);
        }
        return caller;
    }

    protected final String getCallId() {
        if (callId == null) {
            callId = getIntent().getExtras().getString(CallCenterIncomingActivity.CALL_ID);
        }
        return callId;
    }
}
