package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.top.TopActivityCallActivity;

/**
 * Created by ducpv on 1/4/17.
 */

public abstract class RegisterBaseActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivityCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
