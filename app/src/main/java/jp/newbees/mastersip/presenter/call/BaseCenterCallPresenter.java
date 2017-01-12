package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseCenterCallPresenter extends BasePresenter {
    private CenterCallView centerCallView;

    public BaseCenterCallPresenter(Context context, CenterCallView centerCallView) {
        super(context);
        this.centerCallView = centerCallView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckIncomingCallTask) {
            Map<String, Object> result = (Map<String, Object>) task.getDataResponse();
            int callType = (int) result.get(CheckIncomingCallTask.INCOMING_CALL_TYPE);
            UserItem caller = (UserItem) result.get(CheckIncomingCallTask.CALLER);
            handleIncomingCallType(callType, caller);
        }else if(task instanceof CheckCallTask) {

        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.INCOMING_CALL:
                onIncomingCall(receivingCallEvent.getCallerExtension());
                break;
            case ReceivingCallEvent.OUTGOING_CALL:
                onOutgoingCall(receivingCallEvent.getCallerExtension());
                break;
            default:
                break;
        }
    }

    private void handleIncomingCallType(int callType, UserItem caller) {
        switch (callType) {
            case Constant.API.VOICE_CALL:
                centerCallView.incomingVoiceCall(caller);
                break;
            case Constant.API.VIDEO_CALL:
                centerCallView.incomingVideoCall(caller);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                centerCallView.incomingVideoChatCall(caller);
                break;
            default:
                break;
        }
    }

    private void onIncomingCall(String callerExtension) {
        String calleeExtension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        CheckIncomingCallTask checkCallTask = new CheckIncomingCallTask(context, callerExtension, calleeExtension);
        requestToServer(checkCallTask);
    }

    private void onOutgoingCall(String calleeExtension) {
        UserItem callee = ConfigManager.getInstance().getCurrentCallee(calleeExtension);
        centerCallView.outgoingVoiceCall(callee);
    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public interface CenterCallView {
        void incomingVoiceCall(UserItem caller);

        void incomingVideoCall(UserItem caller);

        void incomingVideoChatCall(UserItem caller);

        void outgoingVoiceCall(UserItem callee);
    }
}
