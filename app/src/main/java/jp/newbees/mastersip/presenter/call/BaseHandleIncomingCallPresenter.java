package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.linphone.core.CallDirection;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 * use for handler incoming for Incoming video, chat, voice activity
 */

public class BaseHandleIncomingCallPresenter extends BaseHandleCallPresenter {
    private final int callType;

    public BaseHandleIncomingCallPresenter(Context context, CallView view, int callType) {
        super(context, view);
        this.callType = callType;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        // handle response task
    }

    @Override

    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        // handle error task
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReceivingIncomingCallEvent(ReceivingCallEvent event) {
        if (incomingVoiceCall(event) || incomingVideoOrVideoChatCall(event)) {
            super.handleCallConnected();
        }
    }

    private boolean incomingVideoOrVideoChatCall(ReceivingCallEvent event) {
        return event.getDirection() == CallDirection.Incoming
                && (callType == Constant.API.VIDEO_CALL
                || callType == Constant.API.VIDEO_CHAT_CALL)
                && event.getCallEvent() == ReceivingCallEvent.STREAMS_RUNNING;
    }

    private boolean incomingVoiceCall(ReceivingCallEvent event) {
        return event.getDirection() == CallDirection.Incoming
                && callType == Constant.API.VOICE_CALL
                && event.getCallEvent() == ReceivingCallEvent.INCOMING_CONNECTED_CALL;
    }
}
