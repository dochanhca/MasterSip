package jp.newbees.mastersip.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.CallPresenter;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ProfileDetailItemActivity extends WrapperWithBottomNavigationActivity
        {
    private UserItem userItem;
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
    }

    @Override
    public void onBackPressed() {
        changeHeaderText(userItem.getUsername());
        super.onBackPressed();
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type) {
        super.didSendMsgRequestEnableSettingCall(type);
        TextDialog textDialog = new TextDialog.Builder()
                .hideNegativeButton(true)
                .build(CallPresenter.getMessageSendRequestSuccess(getApplicationContext(), userItem, type));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        super.didSendMsgRequestEnableSettingCallError(errorMessage, errorCode);
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

}
