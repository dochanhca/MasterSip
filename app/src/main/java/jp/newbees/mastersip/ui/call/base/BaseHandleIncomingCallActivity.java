package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.view.WindowManager;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.ui.call.IncomingWaitingFragment;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseHandleCallActivity {
    private BaseHandleIncomingCallPresenter presenter;

    protected abstract int getAcceptCallImage();

    protected abstract String getTitleCall();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }

    @Override
    protected BaseHandleCallPresenter getPresenter() {
        presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this, getCallType());
        presenter.registerEvents();
        return presenter;
    }

    @Override
    protected void onShowWaitingFragment() {
        showIncomingWaitingFragment(getCompetitor(), getCallId(), getAcceptCallImage(), getTitleCall(), getCallType());
    }

    private void showIncomingWaitingFragment(UserItem caller, String callId,
                                             int acceptCallImage, String titleCall, int callType) {
        showWaitingFragment(IncomingWaitingFragment.newInstance(caller, callId, acceptCallImage, titleCall, callType));
    }

    @Override
    public void onCallEnd() {
        if (MyLifecycleHandler.getInstance().getNumberOfActivity() == 1) {
            Logger.e(TAG, "we have only calling activity, stop service and destroy app");
            LinphoneService.stopLinphone(this);
        }
        super.onCallEnd();
    }

}
