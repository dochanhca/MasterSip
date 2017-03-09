package jp.newbees.mastersip.ui.call;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseCenterCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class CallCenterIncomingActivity extends BaseActivity implements BaseCenterCallPresenter.CenterCallView {

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
}
