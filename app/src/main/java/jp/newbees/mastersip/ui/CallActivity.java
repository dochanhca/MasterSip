package jp.newbees.mastersip.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.CallMaker;
import jp.newbees.mastersip.presenter.CallPresenter;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.NotifyRunOutOfCoinDialog;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.ui.dialog.SelectVideoCallDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 4/3/17.
 */

public abstract class CallActivity extends BaseActivity implements CallPresenter.CallView,
        TextDialog.OnTextDialogPositiveClick,
        CallMaker, SelectVideoCallDialog.OnSelectVideoCallDialog,
        NotifyRunOutOfCoinDialog.NotifyRunOutOfCoinDialogClick, OneButtonDialog.OneButtonDialogClickListener {

    private static final int CONFIRM_REQUEST_ENABLE_VOICE_CALL = 10;
    private static final int CONFIRM_REQUEST_ENABLE_VIDEO_CALL = 11;
    private static final int CONFIRM_MAKE_VIDEO_CALL = 12;
    private static final int CONFIRM_MAKE_VOICE_CALL = 13;
    protected static final int REQUEST_BUY_POINT = 15;
    private static final int REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL = 99;
    private CallPresenter presenter;
    private boolean isMessageDialogShowing;
    private UserItem callee;
    private UserItem currentProfileShowing;
    private boolean fromProfileDetail;
    private BroadcastReceiver wifiBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerWifiStateChange();
        presenter = new CallPresenter(this.getApplicationContext(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.registerCallEvent();
        LinphoneService.startLinphone(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterCallEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(wifiBroadcastReceiver);
    }

    private void registerWifiStateChange() {
        wifiBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(info != null && info.isConnected()) {
                    LinphoneService.startLinphone(getApplicationContext());
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, intentFilter);
    }

    /**
     * On Dialog notify not enough point positive clicked
     *
     * @param requestCode
     */
    @Override
    public void onTextDialogOkClick(int requestCode) {
        switch (requestCode) {
            case CONFIRM_REQUEST_ENABLE_VOICE_CALL:
                showLoading();
                presenter.sendMessageRequestEnableSettingCall(callee, SendMessageRequestEnableCallTask.Type.VOICE);
                break;
            case CONFIRM_REQUEST_ENABLE_VIDEO_CALL:
                showLoading();
                presenter.sendMessageRequestEnableSettingCall(callee, SendMessageRequestEnableCallTask.Type.VIDEO);
                break;
            case CONFIRM_MAKE_VIDEO_CALL:
                SelectVideoCallDialog.openDialog(getSupportFragmentManager());
                break;
            case CONFIRM_MAKE_VOICE_CALL:
                this.presenter.checkVoiceCall(callee);
                break;
            case REQUEST_BUY_POINT:
                handleBuyPoint();
                break;
            case REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL:
                isMessageDialogShowing = false;
                showMessageDialog(getString(R.string.call_ended));
                break;
            default:
                break;
        }
    }

    private void handleBuyPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.MALE) {
            PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
        }
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVoiceCall(UserItem callee, String callID) {
        OutgoingVoiceActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVideoCall(UserItem callee, String callID) {
        OutgoingVideoVideoActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVideoChatCall(UserItem callee, String callID) {
        OutgoingVideoChatActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public final void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    /**
     * Do not call this method directly
     *
     * @param busyCallEvent
     */
    @Override
    public final void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String message = busyCallEvent.getCallId() + getString(R.string.mess_callee_reject_call);
        String positiveTitle = getString(R.string.back_to_profile_detail);
        OneButtonDialog.showDialog(getSupportFragmentManager(), "", message, "", positiveTitle);
    }

    @Override
    public final void didCheckCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public final void didUserNotEnoughPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        String title, content, positiveTitle;
        if (gender == UserItem.MALE) {
            title = getString(R.string.point_are_missing);
            content = getString(R.string.mess_suggest_buy_point);
            positiveTitle = getString(R.string.add_point);
        } else {
            title = getString(R.string.partner_point_are_missing);
            content = callee.getUsername() + getString(R.string.mess_suggest_missing_point_for_girl);
            positiveTitle = getString(R.string.to_attack);
        }
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_BUY_POINT)
                .setPositiveTitle(positiveTitle)
                .setTitle(title)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public final void didCallHangUpForGirl() {
        if (isMessageDialogShowing) {
            return;
        }
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public final void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin) {
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
    public final void didRunOutOfCoin() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
            isMessageDialogShowing = true;
        }
    }

    @Override
    public final void didAdminHangUpCall() {
        Logger.e(TAG, "did Admin Hangup Call");
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL)
                .hideNegativeButton(true)
                .build(getString(R.string.mess_admin_hang_up_ca));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        isMessageDialogShowing = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BUY_POINT && resultCode == RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
    }

    @Override
    public final void callVideo(UserItem callee, boolean fromProfileDetail) {
        this.callee = callee;
        this.fromProfileDetail = fromProfileDetail;
        TextDialog textDialog;
        if (callee.getSettings().getVideoCall() == SettingItem.OFF) {
            String content = callee.getUsername() + getString(R.string.mr)
                    + getString(R.string.confirm_request_enable_video_call);
            String positive = getResources().getString(R.string.confirm_request_enable_video_call_positive);
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_REQUEST_ENABLE_VIDEO_CALL)
                    .setPositiveTitle(positive)
                    .build(content);
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        } else {
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_MAKE_VIDEO_CALL)
                    .build(getString(R.string.are_you_sure_make_a_video_call));
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        }
    }

    @Override
    public final void callVoice(UserItem callee, boolean fromProfileDetail) {
        this.callee = callee;
        this.fromProfileDetail = fromProfileDetail;
        TextDialog textDialog;
        if (callee.getSettings().getVoiceCall() == SettingItem.OFF) {
            String content = callee.getUsername() + getString(R.string.mr)
                    + getResources().getString(R.string.confirm_request_enable_voice_call);
            String positive = getResources().getString(R.string.confirm_request_enable_voice_call_positive);
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_REQUEST_ENABLE_VOICE_CALL)
                    .setPositiveTitle(positive)
                    .build(content);
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        } else {
           textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_MAKE_VOICE_CALL)
                    .build(getString(R.string.are_you_sure_make_a_voice_call));
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        }

    }

    @Override
    public void chat(UserItem chatter) {
        ChatActivity.startChatActivity(this, chatter);
    }

    @Override
    public void gotoProfileFromActivity(UserItem userItem) {
        if (!this.fromProfileDetail ||
                this.currentProfileShowing == null ||
                !userItem.getUserId().equalsIgnoreCase(this.currentProfileShowing.getUserId())) {
            ProfileDetailItemActivity.startActivity(this, userItem);
        }
    }

    @Override
    public void onSelectedVideoCall(SelectVideoCallDialog.VideoCall videoCall) {
        if (videoCall == SelectVideoCallDialog.VideoCall.VIDEO_VIDEO) {
            this.presenter.checkVideoCall(callee);
        } else {
            this.presenter.checkVideoChatCall(callee);
        }
    }

    @Override
    public void onBuyCoinClick() {
        // Redirect to buy point screen
        PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
    }

    @Override
    public void onCancelBuyCoinClick() {
        isMessageDialogShowing = false;
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type) {
        disMissLoading();
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onOneButtonPositiveClick() {
        this.gotoProfileFromActivity(callee);
    }

    private void showNotifyCoinEarnedForGirl(int total) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.call_ended_bonus_point))
                .append(total)
                .append(getString(R.string.pt))
                .append(getString(R.string.i_acquired_it));
        showMessageDialog(message.toString());
    }

    private void showDialogBuyPointSuccess(Intent data) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.settlement_is_completed))
                .append("\n")
                .append(data.getStringExtra(PaymentFragment.POINT))
                .append(getString(R.string.pt))
                .append(getString(R.string.have_been_granted));
        showMessageDialog(message.toString());
    }

    protected UserItem getCurrentCallee() {
        return this.callee;
    }

    @Override
    public void setShowingProfile(UserItem userItem) {
        this.currentProfileShowing = userItem;
    }
}
