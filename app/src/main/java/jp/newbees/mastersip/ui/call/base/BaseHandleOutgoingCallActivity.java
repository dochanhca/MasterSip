package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseActivity implements BaseHandleOutgoingCallPresenter.OutgoingCallView {
    public static final String CALLEE = "CALLEE";

    private BaseHandleOutgoingCallPresenter presenter;
    private UserItem callee;
    private int callType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        this.callType = getCallType();
        this.presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
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

    protected final UserItem getCallee() {
        if (callee == null) {
            this.callee = getIntent().getExtras().getParcelable(CALLEE);
        }
        return callee;
    }

    protected abstract int getCallType();
}
