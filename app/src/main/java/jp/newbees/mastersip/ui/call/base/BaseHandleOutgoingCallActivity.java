package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseActivity implements BaseHandleOutgoingCallPresenter.OutgoingCallView{
    private BaseHandleOutgoingCallPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(),this);
        presenter.registerEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
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

    public final void changeCamera() {

    }

    public final void enableCamera(boolean enableCamera) {

    }
}
