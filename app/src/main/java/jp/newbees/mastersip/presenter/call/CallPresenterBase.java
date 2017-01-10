package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import jp.newbees.mastersip.eventbus.CallEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 */

public class CallPresenterBase extends BasePresenter {
    private CallView view;
    public CallPresenterBase(Context context, CallView view) {
        super(context);
        this.view = view;
    }



    public interface CallView {
        void incomingVoiceCall(UserItem caller);

        void incomingVideoCall(UserItem caller);

        void incomingVideoChatCall(UserItem caller);
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

    private void handleIncomingCallType(int callType, UserItem caller){
        switch (callType){
            case Constant.API.VOICE_CALL:
                view.incomingVoiceCall(caller);
                break;
            case Constant.API.VIDEO_CALL:
                view.incomingVideoCall(caller);
                break;
            case Constant.API.VIDEO_CHAT:
                view.incomingVideoChatCall(caller);
                break;
            default:
                break;
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(CallEvent callEvent) {
        switch (callEvent.getCallEvent()) {
            case CallEvent.OUTGOING_CALL:
                onOutgoingCall();
                break;
            case CallEvent.INCOMING_CALL:
                onIncomingCall(callEvent.getCallerExtension());
                break;
            case CallEvent.ACTION_ACCEPT_CALL:
                onAcceptCall(callEvent.getCallerExtension());
                break;
            case CallEvent.CANCEL_CALL:
                onCancelCall(Constant.Error.VOIP_ERROR, "Call canceled");
                break;
            case CallEvent.BUSY_CALL:
                onBusyCall();
                break;
        }
    }

    protected void onIncomingCall(String callerExtension) {
        String calleeExtension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        CheckIncomingCallTask checkCallTask = new CheckIncomingCallTask(context, callerExtension, calleeExtension);
        requestToServer(checkCallTask);
    }

    protected void onOutgoingCall() {
        // TO DO
    }

    protected void onBusyCall() {
        //TO DO
    }

    protected void onAcceptCall(String callerExtension) {
        //TO DO
    }

    protected void onCancelCall(int errorCode, String messageCode) {
        //TO DO
    }

}
