package jp.newbees.mastersip.ui;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.CallPresenterBase;
import jp.newbees.mastersip.ui.call.IncomingVoiceActivity;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseCallActivity extends BaseActivity implements CallPresenterBase.CallView {

    private static final String CALLER = "CALLER";

    private CallPresenterBase callPresenterBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callPresenterBase = new CallPresenterBase(getApplicationContext(),this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callPresenterBase.registerCallEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callPresenterBase.unRegisterCallEvent();
    }

    @Override
    public void incomingVoiceCall(UserItem caller) {
        Intent intent = new Intent(this, IncomingVoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void incomingVideoCall(UserItem caller) {

    }

    @Override
    public void incomingVideoChatCall(UserItem caller) {

    }
}
