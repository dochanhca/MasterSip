package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by ducpv on 12/7/16.
 */

public class TipPageActivity extends BaseActivity implements View.OnClickListener {

    private Button btnRegisterInfo;

    @Override
    protected int layoutId() {
        return R.layout.activity_tip_page;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        btnRegisterInfo = (Button) findViewById(R.id.btn_register_info);

        btnRegisterInfo.setOnClickListener(this);

        initHeader(getString(R.string.tip_page_activity));

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterInfoActivity.class);
        startActivity(intent);
    }
}
