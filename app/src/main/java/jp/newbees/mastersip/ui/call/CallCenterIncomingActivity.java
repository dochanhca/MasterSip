package jp.newbees.mastersip.ui.call;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseCenterCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.NotifyRunOutOfCoinDialog;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class CallCenterIncomingActivity extends BaseActivity implements BaseCenterCallPresenter.CenterCallView,
        NotifyRunOutOfCoinDialog.NotifyRunOutOfCoinDialogClick {

    public static final String CALLER = "CALLER";
    public static final String CALL_ID = "CALL_ID";
    private BaseCenterCallPresenter incomingCallPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingCallPresenter = new BaseCenterCallPresenter(getApplicationContext(), this);
        incomingCallPresenter.registerCallEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        incomingCallPresenter.unRegisterCallEvent();
    }

    @Override
    public void incomingVoiceCall(UserItem caller, String callID) {
        Intent intent = new Intent(getApplicationContext(), IncomingVoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        bundle.putString(CALL_ID, callID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void incomingVideoCall(UserItem caller, String callID) {
        //TODO : Next sprint
    }

    @Override
    public void incomingVideoChatCall(UserItem caller, String callID) {
        //TODO : Next sprint
    }

    @Override
    public void outgoingVoiceCall(UserItem callee) {
        Intent intent = new Intent(getApplicationContext(), OutgoingVoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(OutgoingVoiceActivity.CALLEE, callee);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didCallHangUpForGirl() {
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public void didCoinChangedAfterHangUp(int totalCoinChanged) {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.FEMALE && totalCoinChanged > 0) {
            showNotifyCoinEarnedForGirl(totalCoinChanged);
        } else {
            showMessageDialog(getString(R.string.call_ended));
        }
    }

    @Override
    public void didRunOutOfCoin() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
        }
    }

    @Override
    public void onPositiveButtonClick() {
        // Redirect to buy point screen
    }

    @Override
    public void onNegativeButtonClick() {
        showMessageDialog(getString(R.string.call_ended));
    }

    private void showNotifyCoinEarnedForGirl(int total) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.call_ended_bonus_point))
                .append(total)
                .append(getString(R.string.pt))
                .append(getString(R.string.i_acquired_it));
        showMessageDialog(message.toString());
    }
}
