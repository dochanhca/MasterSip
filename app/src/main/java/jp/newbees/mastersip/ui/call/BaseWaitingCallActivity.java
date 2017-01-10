package jp.newbees.mastersip.ui.call;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseWaitingCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseWaitingCallActivity extends BaseActivity implements BaseWaitingCallPresenter.IncomingCallView {

    private static final String CALLER = "CALLER";
    private BaseWaitingCallPresenter incomingCallPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingCallPresenter = new BaseWaitingCallPresenter(getApplicationContext(),this);
        incomingCallPresenter.registerCallEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        incomingCallPresenter.unRegisterCallEvent();
    }

    @Override
    public void incomingVoiceCall(UserItem caller) {
        Intent intent = new Intent(this, IncomingVoiceCallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void incomingVideoCall(UserItem caller) {
        //TODO : Next sprint
    }

    @Override
    public void incomingVideoChatCall(UserItem caller) {
        //TODO : Next sprint
    }

}
