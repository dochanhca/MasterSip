package jp.newbees.mastersip.ui.call;

import android.os.Bundle;

import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class IncomingVideoVideoActivity extends BaseHandleIncomingCallActivity {
    @Override
    protected int layoutId() {
        return 0;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onCallConnected() {

    }

    @Override
    public void onCoinChanged(int coint) {

    }

    @Override
    protected int getCallType() {
        return Constant.API.VIDEO_CALL;
    }
}
