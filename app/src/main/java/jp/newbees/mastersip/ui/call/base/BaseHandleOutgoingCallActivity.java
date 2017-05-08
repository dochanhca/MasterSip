package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.view.WindowManager;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.ui.call.OutgoingWaitingFragment;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseHandleCallActivity {
    private BaseHandleOutgoingCallPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }

    @Override
    protected BaseHandleCallPresenter getPresenter() {
        presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        return presenter;
    }

    protected abstract String getTextTitleInWaitingFragment();

    @Override
    protected void onShowWaitingFragment() {
        showOutgoingWaitingFragment(getCompetitor(), getTextTitleInWaitingFragment(), getCallType(), getCallId());
    }

    private void showOutgoingWaitingFragment(UserItem callee, String titleCall, int callType, String callId) {
        showWaitingFragment(OutgoingWaitingFragment.newInstance(callee, callId, titleCall, callType));
    }
}
