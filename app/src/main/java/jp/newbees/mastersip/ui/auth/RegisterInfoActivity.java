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

public class RegisterInfoActivity extends BaseActivity implements View.OnClickListener {

    private Button btnRegisterInfo;

    @Override
    protected int layoutId() {
        return R.layout.activity_register_info;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        btnRegisterInfo = (Button) findViewById(R.id.btn_register_info);

        btnRegisterInfo.setOnClickListener(this);

        initHeader(getString(R.string.register_info_activity));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);
        startActivity(intent);
    }
}
