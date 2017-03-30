package jp.newbees.mastersip.presenter.call;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.network.api.ReconnectCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by thangit14 on 3/29/17.
 */

public class CenterIncomingCallPresenter extends BasePresenter {
    private IncomingCallListener incomingCallListener;

    public CenterIncomingCallPresenter(Context context, IncomingCallListener incomingCallListener) {
        super(context);
        this.incomingCallListener = incomingCallListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckIncomingCallTask) {
            Map<String, Object> result = (Map<String, Object>) task.getDataResponse();
            int callType = (int) result.get(CheckIncomingCallTask.INCOMING_CALL_TYPE);
            UserItem caller = (UserItem) result.get(CheckIncomingCallTask.CALLER);
            String callID = (String) result.get(CheckIncomingCallTask.CALL_ID);
            handleIncomingCallType(callType, caller, callID);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof CheckIncomingCallTask) {
            incomingCallListener.didCheckCallError(errorCode,errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.INCOMING_CALL:
                onIncomingCall();
                break;
            default:
                break;
        }
    }

    /**
     * @param event listener Register VoIP response
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterVoIPEvent(RegisterVoIPEvent event) {
        Logger.e(TAG, "onRegisterVoIPEvent receive: " + event.getResponseCode());
        if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_SUCCESS) {
            if (!MyLifecycleHandler.isApplicationInForeground()) {
                saveLoginState(true);
            }
        } else {
            stopLinphoneService();
        }
    }

    private void reconnectRoom(String callId) {
        ReconnectCallTask reconnectCallTask = new ReconnectCallTask(context,
                callId);
        requestToServer(reconnectCallTask);
    }

    private void stopLinphoneService() {
        Intent intent = new Intent(context, LinphoneService.class);
        context.stopService(intent);
    }

    private void saveLoginState(boolean loginState) {
        ConfigManager.getInstance().saveLoginFlag(loginState);
    }

    private void handleIncomingCallType(int callType, UserItem caller, String callID) {
        switch (callType) {
            case Constant.API.VOICE_CALL:
                incomingCallListener.incomingVoiceCall(caller, callID);
                break;
            case Constant.API.VIDEO_CALL:
                incomingCallListener.incomingVideoCall(caller, callID);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                incomingCallListener.incomingVideoChatCall(caller, callID);
                break;
            default:
                break;
        }
    }

    private void onIncomingCall() {
        String calleeExtension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        CheckIncomingCallTask checkCallTask = new CheckIncomingCallTask(context, calleeExtension);
        requestToServer(checkCallTask);
    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public interface IncomingCallListener {

        void incomingVoiceCall(UserItem caller, String callID);

        void incomingVideoCall(UserItem caller, String callID);

        void incomingVideoChatCall(UserItem caller, String callID);

        void didCheckCallError(int errorCode, String errorMessage);
    }
}
