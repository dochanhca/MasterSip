package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;

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
        updateUIWhenInCall();
    }

    public static void startActivity(Context context, UserItem competitor, String callID) {
        Intent intent = new Intent(context, IncomingVoiceActivity.class);
        intent.putExtras(getBundle(competitor,callID));
        context.startActivity(intent);
    }

    public static void startActivityWithNewTask(Context context, UserItem competitor, String callID) {
        Intent intent = new Intent(context, IncomingVoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RUN_FROM, RUN_FROM_BG);
        intent.putExtras(getBundle(competitor,callID));
        context.startActivity(intent);
    }
}
