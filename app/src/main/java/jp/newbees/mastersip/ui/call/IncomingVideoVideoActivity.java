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

public class IncomingVideoVideoActivity extends BaseHandleIncomingCallActivity {

    @Override
    protected int getAcceptCallImage() {
        return R.drawable.ic_accept_video_call;
    }

    @Override
    protected String getTitleCall() {
        return getResources().getString(R.string.titile_in_coming_video_call);
    }

    @Override
    public int getCallType() {
        return Constant.API.VIDEO_CALL;
    }

    @Override
    public void onStreamingConnected() {
        updateUIWhenInCall();
    }

    public static void startActivity(Context context, UserItem competitor, String callID) {
        Intent intent = new Intent(context, IncomingVideoVideoActivity.class);
        intent.putExtras(getBundle(competitor,callID));
        context.startActivity(intent);
    }

    public static void startActivityWithNewTask(Context context, UserItem caller, String callID) {
        Intent intent = new Intent(context, IncomingVideoVideoActivity.class);
        intent.putExtras(getBundle(caller,callID));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RUN_FROM, RUN_FROM_BG);
        context.startActivity(intent);
    }
}
