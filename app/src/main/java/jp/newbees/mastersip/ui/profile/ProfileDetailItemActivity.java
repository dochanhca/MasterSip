package jp.newbees.mastersip.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.call.BaseCenterOutgoingCallPresenter;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ProfileDetailItemActivity extends WrapperWithBottomNavigationActivity
        implements BaseCenterOutgoingCallPresenter.OutgoingCallListener,
        TextDialog.OnTextDialogPositiveClick {

    private static final int REQUEST_NOTIFY_NOT_ENOUGH_POINT = 1;

    private UserItem userItem;
    private BaseCenterOutgoingCallPresenter outgoingCallPresenter;
    private ProfileDetailItemFragment fragment;

    public static void startActivity(Context context, UserItem userItem) {
        Intent intent = new Intent(context, ProfileDetailItemActivity.class);
        intent.putExtra(ProfileDetailItemFragment.USER_ITEM, (Parcelable) userItem);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        userItem = getIntent().getParcelableExtra(ProfileDetailItemFragment.USER_ITEM);
        fragment = ProfileDetailItemFragment.newInstance(userItem, false);
        initHeader(userItem.getUsername());
        showFragmentContent(fragment, ProfileDetailItemFragment.class.getName());

        outgoingCallPresenter = new BaseCenterOutgoingCallPresenter(this, this) {
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        outgoingCallPresenter.unRegisterEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        outgoingCallPresenter.registerEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTIFY_NOT_ENOUGH_POINT && resultCode == Activity.RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
    }

    @Override
    public void onBackPressed() {
        changeHeaderText(userItem.getUsername());
        super.onBackPressed();
    }

    @Override
    public void outgoingVoiceCall(UserItem callee, String callID) {
        OutgoingVoiceActivity.startActivity(ProfileDetailItemActivity.this, callee, callID);
    }

    @Override
    public void outgoingVideoCall(UserItem callee, String callID) {
        OutgoingVideoVideoActivity.startActivity(ProfileDetailItemActivity.this, callee, callID);
    }

    @Override
    public void outgoingVideoChatCall(UserItem callee, String callID) {
        OutgoingVideoChatActivity.startActivity(ProfileDetailItemActivity.this, callee, callID);
    }

    @Override
    public void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(ProfileDetailItemActivity.this, errorCode, errorMessage);
    }

    @Override
    public void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String message = busyCallEvent.getHandleName() + getString(R.string.mess_callee_reject_call);
        String positiveTitle = getString(R.string.back_to_profile_detail);
        OneButtonDialog.showDialog(getSupportFragmentManager(), "", message, "", positiveTitle);
    }

    @Override
    public void didCheckCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    public void didUserNotEnoughPoint(String title, String content, String positiveTitle) {
        TextDialog.openTextDialog(getSupportFragmentManager(), REQUEST_NOTIFY_NOT_ENOUGH_POINT,
                content, title, positiveTitle, false);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type) {

    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {

    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        super.onTextDialogOkClick(requestCode);
        if (requestCode == REQUEST_NOTIFY_NOT_ENOUGH_POINT) {
            PaymentActivity.startActivityForResult(this, REQUEST_NOTIFY_NOT_ENOUGH_POINT);
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

    public BaseCenterOutgoingCallPresenter getOutgoingCallPresenter() {
        return outgoingCallPresenter;
    }
}
