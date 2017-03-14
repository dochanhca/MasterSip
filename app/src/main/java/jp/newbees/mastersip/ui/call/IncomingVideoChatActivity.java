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

public class IncomingVideoChatActivity extends BaseHandleIncomingCallActivity {

    @Override
    protected int getAcceptCallImage() {
        return R.drawable.bg_ic_accept_call;
    }

    @Override
    protected String getTitleCall() {
        return "Need update";
    }

    @Override
    protected int getCallType() {
        return Constant.API.VIDEO_CHAT_CALL;
    }

    @Override
    public void onCallConnected() {

    }


    public static void startActivity(Context context, UserItem caller, String callID) {
        Intent intent = new Intent(context, IncomingVideoChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        bundle.putString(CALL_ID, callID);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
