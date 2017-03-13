package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.BaseHandleOutgoingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
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

    @Override
    public void onCallConnected() {
        countingCallDuration();
        updateView();
    }

    private void updateView() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }
        imgLoading.setVisibility(View.GONE);
    }
}
