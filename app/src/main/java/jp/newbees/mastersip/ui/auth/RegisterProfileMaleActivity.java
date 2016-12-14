package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.top.TopActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterProfileMaleActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected int layoutId() {
        return R.layout.activity_register_profile_male;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

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
