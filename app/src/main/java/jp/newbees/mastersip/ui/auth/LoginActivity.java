package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.top.TopActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button btnLogin;

    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initHeader(getString(R.string.login_activity));

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        goTopActivity();
    }

    private void goTopActivity() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);
        startActivity(intent);
    }
}
