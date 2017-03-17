package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class IncomingVoiceActivity extends BaseHandleIncomingCallActivity {

    @Override
    protected int getAcceptCallImage() {
        return R.drawable.bg_ic_accept_call;
    }

    @Override
    protected String getTitleCall() {
        return getResources().getString(R.string.in_coming_voice_call);
    }

    @Override
    public int getCallType() {
        return Constant.API.VOICE_CALL;
    }

    @Override
    public void onCallConnected() {
        countingCallDuration();
        updateSpeaker();
        showCallingViewOnVoiceCall();
    }

    public static void startActivity(Context context, UserItem caller, String callID) {
        Intent intent = new Intent(context, IncomingVoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        bundle.putString(CALL_ID, callID);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
