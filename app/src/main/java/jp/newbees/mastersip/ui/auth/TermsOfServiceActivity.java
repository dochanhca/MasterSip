package jp.newbees.mastersip.ui.auth;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by ducpv on 12/7/16.
 */

public class TermsOfServiceActivity extends BaseActivity {
    @Override
    protected int layoutId() {
        return R.layout.activity_terms_of_service;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initHeader(getString(R.string.terms_of_service));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }
}
