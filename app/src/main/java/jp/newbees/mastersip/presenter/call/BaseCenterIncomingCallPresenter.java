package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import jp.newbees.mastersip.event.call.AdminHangUpEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.HangUpForGirlEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.network.api.ReconnectCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/10/17.
 */

public class BaseCenterIncomingCallPresenter extends BasePresenter {
    private CenterCallView centerCallView;

    public BaseCenterIncomingCallPresenter(Context context, CenterCallView centerCallView) {
        super(context);
        this.centerCallView = centerCallView;
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
        if (task instanceof ReconnectCallTask) {
            centerCallView.didConnectCallError(errorCode, errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.INCOMING_CALL:
                onIncomingCall();
                break;
            case ReceivingCallEvent.OUTGOING_CALL:
                reconnectRoom();
                onOutgoingCall(receivingCallEvent.getCallId());
                break;
            default:
                break;
        }
    }

    /**
     * Listen when call ended less than 1 minute
     *
     * @param event
     */
    @Subscribe()
    public void onHangUpForGirlEvent(HangUpForGirlEvent event) {
        centerCallView.didCallHangUpForGirl();
    }

    @Subscribe()
    public void onCoinChangedEvent(CoinChangedEvent event) {
        centerCallView.didCoinChangedAfterHangUp(event.getTotal(), event.getCoin());
    }

    @Subscribe()
    public void onRunOutOfCoinEvent(RunOutOfCoinEvent event) {
        centerCallView.didRunOutOfCoin();
    }

    @Subscribe()
    public void onAdminHangUpEvent(AdminHangUpEvent event) {
        centerCallView.didAdminHangUpCall();
    }

    private void reconnectRoom() {
        ReconnectCallTask reconnectCallTask = new ReconnectCallTask(context,
                ConfigManager.getInstance().getCallId());
        requestToServer(reconnectCallTask);
    }

    private void handleIncomingCallType(int callType, UserItem caller, String callID) {
        switch (callType) {
            case Constant.API.VOICE_CALL:
                centerCallView.incomingVoiceCall(caller, callID);
                break;
            case Constant.API.VIDEO_CALL:
                centerCallView.incomingVideoCall(caller, callID);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                centerCallView.incomingVideoChatCall(caller, callID);
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

    private void onOutgoingCall(String callId) {
        UserItem callee = ConfigManager.getInstance().getCurrentCallee(callId);
        int callType = ConfigManager.getInstance().getCurrentCallType();
        switch (callType) {
            case Constant.API.VOICE_CALL:
                centerCallView.outgoingVoiceCall(callee);
                break;
            case Constant.API.VIDEO_CALL:
                centerCallView.outgoingVideoCall(callee);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                centerCallView.outgoingVideoChatCall(callee);
                break;
        }
    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public interface CenterCallView {
        void incomingVoiceCall(UserItem caller, String callID);

        void incomingVideoCall(UserItem caller, String callID);

        void incomingVideoChatCall(UserItem caller, String callID);

        void outgoingVoiceCall(UserItem callee);

        void outgoingVideoCall(UserItem callee);

        void outgoingVideoChatCall(UserItem callee);

        void didConnectCallError(int errorCode, String errorMessage);

        void didCallHangUpForGirl();

        void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin);

        void didRunOutOfCoin();

        void didAdminHangUpCall();
    }
}
