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
    protected int layoutId() {
        return R.layout.activity_out_going_video_video;
    }


    @Override
    protected int getCallType() {
        return Constant.API.VIDEO_CALL;
    }

    public static void startActivity(Context context, UserItem callee) {
        Intent intent = new Intent(context, OutgoingVideoVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLEE, callee);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
