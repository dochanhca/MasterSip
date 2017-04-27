package jp.newbees.mastersip.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.AdminHangUpEvent;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.HangUpForGirlEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.ReconnectCallTask;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 4/3/17.
 */

public class CallPresenter extends BasePresenter {
    private CallView callView;

    public interface CallView {

        void outgoingVoiceCall(UserItem callee, String callID);

        void outgoingVideoCall(UserItem callee, String callID);

        void outgoingVideoChatCall(UserItem callee, String callID);

        void didConnectCallError(int errorCode, String errorMessage);

        void onCalleeRejectCall(BusyCallEvent busyCallEvent);

        void didCheckCallError(int errorCode, String errorMessage);

        void didUserNotEnoughPoint();

        void didSendMsgRequestEnableSettingCall(int type);

        void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode);

        void didCallHangUpForGirl();

        void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin);

        void didRunOutOfCoin();

        void didAdminHangUpCall();

        void didCheckedIncomingVoiceCall(UserItem callUser, String callId);

        void didCheckedIncomingVideoCall(UserItem callUser, String callId);

        void didCheckedIncomingVideoChatCall(UserItem callUser, String callId);
    }

    public CallPresenter(Context context, CallView callView) {
        super(context);
        this.callView = callView;
    }

    public final void unregisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    public final void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ReconnectCallTask) {
            callView.didConnectCallError(errorCode, errorMessage);
        } else if (task instanceof CheckCallTask) {
            handleCheckCallError(errorCode, errorMessage);
        } else if (task instanceof SendMessageRequestEnableCallTask) {
            callView.didSendMsgRequestEnableSettingCallError(errorMessage, errorCode);
        }
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ReconnectCallTask) {
            Logger.e(tag, "Reconnect call task successful");
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        } else if (task instanceof SendMessageRequestEnableCallTask) {
            int type = ((SendMessageRequestEnableCallTask) task).getDataResponse();
            callView.didSendMsgRequestEnableSettingCall(type);
        }
    }

    private void handleResponseCheckCall(BaseTask task) {
        HashMap<String, Object> result = (HashMap<String, Object>) task.getDataResponse();
        int callType = (int) result.get(CheckCallTask.CALL_TYPE);
        UserItem callee = (UserItem) result.get(CheckCallTask.CALLEE);

        String callId = (String) result.get(CheckCallTask.CALL_ID);
        ConfigManager.getInstance().setCallId(callId);
        String roomId = (String) result.get(CheckCallTask.ROOM_FREE);

        ConfigManager.getInstance().updateEndCallStatus(false);

        ConfigManager.getInstance().setCurrentCallUser(callee, callId);
        makeCall(roomId, callType);
    }

    private void makeCall(String roomId, int callType) {
        LinphoneHandler.getInstance().makeCall(callType, roomId);
    }

    private void handleCheckCallError(int errorCode, String errorMessage) {
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            callView.didUserNotEnoughPoint();
        } else {
            callView.didCheckCallError(errorCode, errorMessage);
        }
    }

    /**
     * Check callee before make a voice call
     *
     * @param callee
     */
    public final void checkVoiceCall(UserItem callee) {
        this.checkCall(callee, Constant.API.VOICE_CALL);
    }

    /**
     * Check callee before make a video call
     *
     * @param callee
     */
    public final void checkVideoCall(UserItem callee) {
        this.checkCall(callee, Constant.API.VIDEO_CALL);
    }

    /**
     * Check callee before make a video chat call00.0.0.
     *
     * @param callee
     */
    public final void checkVideoChatCall(UserItem callee) {
        this.checkCall(callee, Constant.API.VIDEO_CHAT_CALL);
    }

    /**
     * Send request check call to server
     *
     * @param callee
     * @param callType
     */
    private void checkCall(UserItem callee, int callType) {
        UserItem caller = getCurrentUserItem();
        CheckCallTask checkCallTask = new CheckCallTask(context, caller, callee, callType, Constant.API.CALL_FROM_OTHER);
        requestToServer(checkCallTask);
    }

    /**
     * Listen when call ended less than 1 minute
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHangUpForGirlEvent(HangUpForGirlEvent event) {
        callView.didCallHangUpForGirl();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        callView.didCoinChangedAfterHangUp(event.getTotal(), event.getCoin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunOutOfCoinEvent(RunOutOfCoinEvent event) {
        callView.didRunOutOfCoin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAdminHangUpEvent(AdminHangUpEvent event) {
        callView.didAdminHangUpCall();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusyCallEvent(BusyCallEvent busyCallEvent) {
        callView.onCalleeRejectCall(busyCallEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent event) {
        switch (event.getCallEvent()) {
            case ReceivingCallEvent.OUTGOING_CALL:
//                notifyCallerJoinedRoom(event.getCallId());
                handleOutgoingCall(event.getCallId());
                break;
            case ReceivingCallEvent.CHECKED_INCOMING_VOICE_CALL:
                callView.didCheckedIncomingVoiceCall(event.getCallUser(), event.getCallId());
                break;
            case ReceivingCallEvent.CHECKED_INCOMING_VIDEO_CALL:
                callView.didCheckedIncomingVideoCall(event.getCallUser(), event.getCallId());
                break;
            case ReceivingCallEvent.CHECKED_INCOMING_VIDEO_CHAT_CALL:
                callView.didCheckedIncomingVideoChatCall(event.getCallUser(), event.getCallId());
                break;
            default:
                break;
        }
    }

    /**
     * Notify to server that caller has joined a room.
     *
     * @param callId
     */
    private void notifyCallerJoinedRoom(String callId) {
        ReconnectCallTask reconnectCallTask = new ReconnectCallTask(context,
                callId);
        requestToServer(reconnectCallTask);
    }

    /**
     * Callbacks to View after caller has joined a room
     *
     * @param callId
     */
    private void handleOutgoingCall(String callId) {
        UserItem callee = ConfigManager.getInstance().getCurrentCallee(callId);
        int callType = ConfigManager.getInstance().getCurrentCallType();
        switch (callType) {
            case Constant.API.VOICE_CALL:
                callView.outgoingVoiceCall(callee, callId);
                break;
            case Constant.API.VIDEO_CALL:
                callView.outgoingVideoCall(callee, callId);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                callView.outgoingVideoChatCall(callee, callId);
                break;
            default:
                break;
        }
    }

    public final void sendMessageRequestEnableSettingCall(UserItem userItem, int type) {
        SendMessageRequestEnableCallTask task = new SendMessageRequestEnableCallTask(context, userItem, type);
        requestToServer(task);
    }

    public final static String getMessageSendRequestSuccess(Context context, UserItem userItem, int type) {
        String message = "";
        switch (type) {
            case Constant.API.VOICE_CALL:
                message = String.format(context.getString(R.string.message_request_enable_voice_success), userItem.getUsername());
                break;
            case Constant.API.VIDEO_CALL:
                message = String.format(context.getString(R.string.message_request_enable_video_success), userItem.getUsername());
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                message = String.format(context.getString(R.string.message_request_enable_video_chat_success), userItem.getUsername());
                break;
            default:
                break;
        }
        return message;
    }
}
