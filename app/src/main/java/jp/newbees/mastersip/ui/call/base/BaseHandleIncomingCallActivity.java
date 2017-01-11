package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseActivity implements BaseHandleIncomingCallPresenter.IncomingCallView {

    private BaseHandleIncomingCallPresenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    public final void rejectCall() {
        this.presenter.rejectCall();
    }

    public final void acceptCall() {
        this.presenter.acceptCall();
    }

    public final void endCall() {
        this.presenter.endCall();
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

}
