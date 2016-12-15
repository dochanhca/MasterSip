package jp.newbees.mastersip.ui.auth;

import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by ducpv on 12/14/16.
 */

public class PickLocationActivity extends BaseActivity {
    public static final int PICK_LOCATION_REQUEST_CODE = 12;

    @Override
    protected int layoutId() {
        return R.layout.activity_pick_location;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.pick_location));

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }
}
