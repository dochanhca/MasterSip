package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.call.BaseCenterOutgoingCallPresenter;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.ConfirmVoiceCallDialog;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.ui.dialog.SelectVideoCallDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;

/**
 * Created by vietbq on 3/30/17.
 */

public abstract class BaseCallFragment extends BaseFragment implements
        TextDialog.OnTextDialogPositiveClick,
        SelectVideoCallDialog.OnSelectVideoCallDialog,
        BaseCenterOutgoingCallPresenter.OutgoingCallListener,
        ConfirmVoiceCallDialog.OnDialogConfirmVoiceCallClick {

    private static final int CONFIRM_VOICE_CALL_DIALOG = 10;
    private static final int CONFIRM_REQUEST_ENABLE_VOICE_CALL = 12;
    private static final int CONFIRM_REQUEST_ENABLE_VIDEO_CALL = 13;
    private static final int CONFIRM_MAKE_VIDEO_CALL = 15;
    private static final int REQUEST_NOTIFY_NOT_ENOUGH_POINT = 1;
    private static final int SELECT_VIDEO_CALL_DIALOG = 14;
    private static final int REQUEST_NOTIFY_CALLEE_REJECT_CALL = 2;

    private BaseCenterOutgoingCallPresenter baseCallPresenter;
    private UserItem currentUserItem;

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
//        baseCallPresenter = new BaseCenterOutgoingCallPresenter(getContext(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        baseCallPresenter.registerEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
//        baseCallPresenter.unRegisterEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTIFY_NOT_ENOUGH_POINT && resultCode == Activity.RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
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

    public final void requestGotoProfile(UserItem userItem) {
        ProfileDetailItemActivity.startActivity(getContext(),userItem);
    }

    public final void requestChatClick(UserItem userItem) {
        ChatActivity.startChatActivity(getContext(), userItem);
    }

    public final void requestVideoCallClick(UserItem userItem) {
        this.currentUserItem = userItem;
        if (userItem.getSettings().getVideoCall() == SettingItem.OFF) {
            String content = userItem.getUsername() + getString(R.string.mr)
                    + getString(R.string.confirm_request_enable_video_call);
            String positive = getResources().getString(R.string.confirm_request_enable_video_call_positive);
            TextDialog.openTextDialog(this, CONFIRM_REQUEST_ENABLE_VIDEO_CALL, getFragmentManager(), content, "", positive);
        } else {
            TextDialog.openTextDialog(this, CONFIRM_MAKE_VIDEO_CALL, getFragmentManager(),
                    getString(R.string.are_you_sure_make_a_video_call), "");
        }
    }

    public final void requestVoiceCallClick(UserItem userItem) {
        this.currentUserItem = userItem;
        if (userItem.getSettings().getVoiceCall() == SettingItem.OFF) {
            String content = userItem.getUsername() + getString(R.string.mr)
                    + getResources().getString(R.string.confirm_request_enable_voice_call);
            String positive = getResources().getString(R.string.confirm_request_enable_voice_call_positive);
            TextDialog.openTextDialog(this, CONFIRM_REQUEST_ENABLE_VOICE_CALL, getFragmentManager(), content, "", positive);
        } else {
            ConfirmVoiceCallDialog.openConfirmVoiceCallDialog(this,
                    CONFIRM_VOICE_CALL_DIALOG, getFragmentManager());
        }
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        UserItem userItem = this.currentUserItem;
        switch (requestCode) {
            case CONFIRM_REQUEST_ENABLE_VOICE_CALL:
                showLoading();
//                baseCallPresenter.sendMessageRequestEnableSettingCall(userItem, SendMessageRequestEnableCallTask.Type.VOICE);
                break;
            case CONFIRM_REQUEST_ENABLE_VIDEO_CALL:
                showLoading();
//                baseCallPresenter.sendMessageRequestEnableSettingCall(userItem, SendMessageRequestEnableCallTask.Type.VIDEO);
                break;
            case CONFIRM_MAKE_VIDEO_CALL:
                SelectVideoCallDialog.openDialog(this, SELECT_VIDEO_CALL_DIALOG, getFragmentManager());
                break;
            case REQUEST_NOTIFY_NOT_ENOUGH_POINT:
                PaymentActivity.startActivityForResult(this, REQUEST_NOTIFY_NOT_ENOUGH_POINT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelectedVideoCall(SelectVideoCallDialog.VideoCall videoCall) {
        UserItem userItem = this.currentUserItem;
        if (videoCall == SelectVideoCallDialog.VideoCall.VIDEO_VIDEO) {
//            baseCallPresenter.checkVideoCall(userItem);
        } else {
//            baseCallPresenter.checkVideoChatCall(userItem);
        }
    }

    @Override
    public void outgoingVoiceCall(UserItem callee, String callID) {
        OutgoingVoiceActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void outgoingVideoCall(UserItem callee, String callID) {
        OutgoingVideoVideoActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void outgoingVideoChatCall(UserItem callee, String callID) {
        OutgoingVideoChatActivity.startActivity(getContext(), callee, callID);
    }

    @Override
    public void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String message = currentUserItem.getUsername() + " " + getString(R.string.mess_callee_reject_call);
        String positiveTitle = getString(R.string.back_to_profile_detail);
        OneButtonDialog.showDialog(this, getFragmentManager(),
                REQUEST_NOTIFY_CALLEE_REJECT_CALL, "", message, "", positiveTitle);
    }

    @Override
    public void didCheckCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didUserNotEnoughPoint(String title, String content, String positiveTitle) {
        TextDialog.openTextDialog(this, REQUEST_NOTIFY_NOT_ENOUGH_POINT, getFragmentManager(),
                content, title, positiveTitle, false);
    }

    @Override
    public void onOkVoiceCallClick() {
//        baseCallPresenter.checkVoiceCall(currentUserItem);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type) {
        disMissLoading();
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        disMissLoading();
    }
}
