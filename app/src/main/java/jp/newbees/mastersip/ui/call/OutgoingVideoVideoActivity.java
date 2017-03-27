package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.BaseHandleOutgoingCallActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class OutgoingVideoVideoActivity extends BaseHandleOutgoingCallActivity {

    @Override
    protected String getTextTitle() {
        return getResources().getString(R.string.title_outgoing_call_video);
    }

    @Override
    public int getCallType() {
        return Constant.API.VIDEO_CALL;
    }

    public static void startActivity(Context context, UserItem callee, String callID) {
        Intent intent = new Intent(context, OutgoingVideoVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(COMPETITOR, callee);
        bundle.putString(CALL_ID, callID);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCallConnected() {
        showVideoCallFragment();
        useFrontCamera();
    }
}
