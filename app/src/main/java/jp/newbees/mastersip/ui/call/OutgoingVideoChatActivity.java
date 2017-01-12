package jp.newbees.mastersip.ui.call;

import android.os.Bundle;

import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.ui.call.base.BaseHandleOutgoingCallActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class OutgoingVideoChatActivity extends BaseHandleOutgoingCallActivity {

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
    public void onCallEnd() {

    }

    @Override
    public void onCoinChanged(CoinChangedEvent event) {

    }

    @Override
    protected int getCallType() {
        return Constant.API.VIDEO_CHAT_CALL;
    }
}
