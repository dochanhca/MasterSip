package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int layoutId() {
        return R.layout.activity_top;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.top_activity));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {

    }
}
