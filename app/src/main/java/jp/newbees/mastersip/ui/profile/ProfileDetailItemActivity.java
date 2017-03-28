package jp.newbees.mastersip.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseCenterOutgoingCallPresenter;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ProfileDetailItemActivity extends WrapperWithBottomNavigationActivity
        implements BaseCenterOutgoingCallPresenter.OutgoingCallListener {

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

        outgoingCallPresenter = new BaseCenterOutgoingCallPresenter(this, this){};
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
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            fragment.showDialogNotifyNotEnoughPoint();
        } else {
            showToastExceptionVolleyError(this, errorCode, errorMessage);
        }
    }

    public BaseCenterOutgoingCallPresenter getOutgoingCallPresenter() {
        return outgoingCallPresenter;
    }
}
