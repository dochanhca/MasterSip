package jp.newbees.mastersip.ui.auth;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by ducpv on 12/7/16.
 */

public class PolicyActivity extends BaseActivity {
    @Override
    protected int layoutId() {
        return R.layout.activity_policy;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.policy));

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }
}
