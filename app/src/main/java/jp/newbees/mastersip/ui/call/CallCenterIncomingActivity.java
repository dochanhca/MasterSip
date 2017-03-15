package jp.newbees.mastersip.ui.call;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseCenterCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.NotifyRunOutOfCoinDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class CallCenterIncomingActivity extends BaseActivity implements BaseCenterCallPresenter.CenterCallView,
        NotifyRunOutOfCoinDialog.NotifyRunOutOfCoinDialogClick, TextDialog.OnTextDialogPositiveClick {

    private static final int REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL = 99;

    private BaseCenterCallPresenter incomingCallPresenter;
    private boolean isMessageDialogShowing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingCallPresenter = new BaseCenterCallPresenter(getApplicationContext(), this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        incomingCallPresenter.registerCallEvent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        incomingCallPresenter.unRegisterCallEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void incomingVoiceCall(UserItem caller, String callID) {
        IncomingVoiceActivity.startActivity(this, caller, callID);
    }

    @Override
    public void incomingVideoCall(UserItem caller, String callID) {
        IncomingVideoVideoActivity.startActivity(this, caller, callID);
    }

    @Override
    public void incomingVideoChatCall(UserItem caller, String callID) {
        IncomingVideoChatActivity.startActivity(this, caller, callID);
    }

    @Override
    public void outgoingVoiceCall(UserItem callee) {
        OutgoingVoiceActivity.startActivity(this, callee);
    }

    @Override
    public void outgoingVideoCall(UserItem callee) {
        OutgoingVideoVideoActivity.startActivity(this, callee);
    }

    @Override
    public void outgoingVideoChatCall(UserItem callee) {
        OutgoingVideoChatActivity.startActivity(this, callee);
    }

    @Override
    public void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didCallHangUpForGirl() {
        if (isMessageDialogShowing) {
            return;
        }
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin) {
        if (isMessageDialogShowing) {
            return;
        }
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.FEMALE && totalCoinChanged > 0) {
            showNotifyCoinEarnedForGirl(totalCoinChanged);
        } else if (gender == UserItem.MALE && currentCoin < Constant.Application.MIN_COIN_FOR_CALL) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
            isMessageDialogShowing = true;
        } else {
            showMessageDialog(getString(R.string.call_ended));
        }
    }

    @Override
    public void didRunOutOfCoin() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
            isMessageDialogShowing = true;
        }
    }

    @Override
    public void didAdminHangUpCall() {
        Logger.e(TAG, "did Admin Hangup Call");
        TextDialog.openTextDialog(getSupportFragmentManager(), REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL,
                getString(R.string.mess_admin_hang_up_ca), "", "", true);
        isMessageDialogShowing = true;
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL) {
            isMessageDialogShowing = false;
            showMessageDialog(getString(R.string.call_ended));
        }
    }

    @Override
    public void onPositiveButtonClick() {
        // Redirect to buy point screen
    }

    @Override
    public void onNegativeButtonClick() {
        isMessageDialogShowing = false;
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
