package jp.newbees.mastersip.ui.call;

import android.os.Bundle;

import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleCallActivity extends BaseActivity implements BaseHandleCallPresenter.HandleCallView {

    private BaseHandleCallPresenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = new BaseHandleCallPresenter(getApplicationContext(), this);
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
