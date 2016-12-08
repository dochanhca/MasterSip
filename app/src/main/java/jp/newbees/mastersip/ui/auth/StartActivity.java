package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class StartActivity extends BaseActivity implements View.OnClickListener {

    private Button btnRegister;
    private Button btnLogin;
    private Button btnFbLogin;

    @Override
    protected int layoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnFbLogin = (Button) findViewById(R.id.btn_fb_login);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFbLogin.setOnClickListener(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                goRegisterDOBActivity();
                break;
            case R.id.btn_login:
                goLoginActivity();
                break;
            case R.id.btn_fb_login:
                implementFbLogin();
                break;
        }
    }

    private void implementFbLogin() {

    }

    private void goLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void goRegisterDOBActivity() {

        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        startActivity(intent);
    }
}
