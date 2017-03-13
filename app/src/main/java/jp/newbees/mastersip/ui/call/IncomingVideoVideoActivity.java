package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
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
    protected int getCallType() {
        return Constant.API.VIDEO_CALL;
    }

    @Override
    public void onCallConnected() {
        countingCallDuration();
        enableSpeaker(btnOnOffSpeaker.isChecked());
        showCallingView();
    }

    /**
     * start when user during a call
     */
    private void showCallingView() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }

        layoutVoiceCallingAction.setVisibility(View.VISIBLE);
        layoutReceivingCallAction.setVisibility(View.GONE);
        imgLoading.setVisibility(View.GONE);
    }

    public static void startActivity(Context context, UserItem caller, String callID) {
        Intent intent = new Intent(context, IncomingVideoVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CALLER, caller);
        bundle.putString(CALL_ID, callID);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
