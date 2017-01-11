package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseWaitingCallPresenter extends BasePresenter {
    private IncomingCallView incomingCallView;

    public BaseWaitingCallPresenter(Context context, IncomingCallView incomingCallView) {
        super(context);
        this.incomingCallView = incomingCallView;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckIncomingCallTask) {
            Map<String, Object> result = (Map<String, Object>) task.getDataResponse();
            int callType = (int) result.get(CheckIncomingCallTask.INCOMING_CALL_TYPE);
            UserItem caller = (UserItem) result.get(CheckIncomingCallTask.CALLER);
            handleIncomingCallType(callType, caller);
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
            default:
                break;
        }
    }

    private void handleIncomingCallType(int callType, UserItem caller) {
        switch (callType) {
            case Constant.API.VOICE_CALL:
                incomingCallView.incomingVoiceCall(caller);
                break;
            case Constant.API.VIDEO_CALL:
                incomingCallView.incomingVideoCall(caller);
                break;
            case Constant.API.VIDEO_CHAT:
                incomingCallView.incomingVideoChatCall(caller);
                break;
            default:
                break;
        }
    }

    protected void onIncomingCall(String callerExtension) {
        String calleeExtension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        CheckIncomingCallTask checkCallTask = new CheckIncomingCallTask(context, callerExtension, calleeExtension);
        requestToServer(checkCallTask);
    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public interface IncomingCallView {
        void incomingVoiceCall(UserItem caller);

        void incomingVideoCall(UserItem caller);

        void incomingVideoChatCall(UserItem caller);
    }
}
