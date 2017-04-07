package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
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
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        setPresenter(presenter);

    }

    protected abstract String getTextTitleInWaitingFragment();

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        super.initVariables(savedInstanceState);

        showOutgoingWaitingFragment(getCompetitor(), getTextTitleInWaitingFragment(), getCallType(), getCallId());
    }

    protected void showVideoCallFragment() {
        if (getVisibleFragment() instanceof OutgoingWaitingFragment) {
            showVideoCallFragment(getCompetitor(),getCallId(),getCallType(),
                    ((OutgoingWaitingFragment) getVisibleFragment()).isSpeakerEnable(),
                    ((OutgoingWaitingFragment) getVisibleFragment()).muteMic());
        }
    }

    private void showOutgoingWaitingFragment(UserItem callee, String titleCall, int callType, String callId) {
        showWaitingFragment(OutgoingWaitingFragment.newInstance(callee, callId, titleCall, callType));
    }
}
