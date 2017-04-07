package jp.newbees.mastersip.ui.call;

import android.content.Context;
import android.content.Intent;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class IncomingVideoChatActivity extends BaseHandleIncomingCallActivity {

    @Override
    protected int getAcceptCallImage() {
        return R.drawable.ic_accept_video_chat_call;
    }

    @Override
    protected String getTitleCall() {
        return getResources().getString(ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE ?
                R.string.title_incoming_call_video_chat_for_male : R.string.title_incoming_call_video_chat_for_female);
    }

    @Override
    public int getCallType() {
        return Constant.API.VIDEO_CHAT_CALL;
    }

    @Override
    public void onCallConnected() {

    }

    public static void startActivity(Context context, UserItem competitor, String callID) {
        Intent intent = new Intent(context, IncomingVideoChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtras(getBundle(competitor,callID));
        context.startActivity(intent);
    }
}
