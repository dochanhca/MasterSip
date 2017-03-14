package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

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
    protected int getCallType() {
        return Constant.API.VIDEO_CALL;
    }

    @Override
    public void onStreamingConnected() {
        startVideoCall();
        showVideoCallFragment();
    }

    private void showVideoCallFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = VideoCallFragment.newInstance();
        transaction.replace(R.id.fragment_container, fragment,
                VideoCallFragment.class.getName()).commit();
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
