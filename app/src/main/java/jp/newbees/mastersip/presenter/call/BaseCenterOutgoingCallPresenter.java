package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CancelCallTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.ReconnectCallTask;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/24/17.
 * Use for listener outgoing call only
 */

public abstract class BaseCenterOutgoingCallPresenter extends BasePresenter {
    private OutgoingCallListener outgoingCallListener;
    private UserItem callee;

    public BaseCenterOutgoingCallPresenter(Context context, OutgoingCallListener outgoingCallListener) {
        super(context);
        this.outgoingCallListener = outgoingCallListener;
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof ReconnectCallTask) {
            outgoingCallListener.didConnectCallError(errorCode, errorMessage);
        } else if (task instanceof CheckCallTask) {
            handleCheckCallError(errorCode, errorMessage);
        }
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof ReconnectCallTask) {
            Logger.e(TAG,"Reconnect call task successful");
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusyCallEvent(BusyCallEvent busyCallEvent) {
        outgoingCallListener.onCalleeRejectCall(busyCallEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        switch (receivingCallEvent.getCallEvent()) {
            case ReceivingCallEvent.OUTGOING_CALL:
                reconnectRoom(receivingCallEvent.getCallId());
                onOutgoingCall(receivingCallEvent.getCallId());
                break;
            case ReceivingCallEvent.LINPHONE_ERROR:
                performCancelCall(receivingCallEvent.getCallId());
                break;
            default:
                break;
        }
    }

    protected void performCancelCall(String calId) {
        CancelCallTask cancelCallTask = new CancelCallTask(context, calId);
        requestToServer(cancelCallTask);
    }

    private void handleCheckCallError(int errorCode, String errorMessage) {
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            genMessageNotifyUserNotEnoughPoint();
        } else {
            outgoingCallListener.didCheckCallError(errorCode, errorMessage);
        }
    }

    private void genMessageNotifyUserNotEnoughPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        String title, content, positiveTitle;
        if (gender == UserItem.MALE) {
            title = context.getString(R.string.point_are_missing);
            content = context.getString(R.string.mess_suggest_buy_point);
            positiveTitle = context.getString(R.string.add_point);
        } else {
            title = context.getString(R.string.partner_point_are_missing);
            content = callee.getUsername() + context.getString(R.string.mess_suggest_missing_point_for_girl);
            positiveTitle = context.getString(R.string.to_attack);
        }
        outgoingCallListener.didUserNotEnoughPoint(title, content, positiveTitle);
    }

    private void handleResponseCheckCall(BaseTask task) {
        HashMap<String, Object> result = (HashMap<String, Object>) task.getDataResponse();
        int callType = (int) result.get(CheckCallTask.CALL_TYPE);
        UserItem callee = (UserItem) result.get(CheckCallTask.CALLEE);

        String callId = (String) result.get(CheckCallTask.CALL_ID);
        ConfigManager.getInstance().setCallId(callId);
        String roomId = (String) result.get(CheckCallTask.ROOM_FREE);

        ConfigManager.getInstance().setCurrentCallee(callee, callId);
        makeCall(roomId, callType);
    }

    private void makeCall(String roomId, int callType) {
        LinphoneHandler.getInstance().makeCall(callType, roomId);
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
        this.callee = callee;
        UserItem caller = getCurrentUserItem();
        CheckCallTask checkCallTask = new CheckCallTask(context, caller, callee, callType, Constant.API.CALL_FROM_OTHER);
        requestToServer(checkCallTask);
    }

    public final void registerEvent() {
        EventBus.getDefault().register(this);
    }

    public final void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void sendMessageRequestEnableSettingCall(UserItem userItem, SendMessageRequestEnableCallTask.Type type) {
        SendMessageRequestEnableCallTask task = new SendMessageRequestEnableCallTask(context, userItem, type);
        requestToServer(task);
    }

    public String getMessageSendRequestSuccess(UserItem userItem,SendMessageRequestEnableCallTask.Type type) {
        String message = "";
        switch (type) {
            case VOICE:
                message = String.format(context.getString(R.string.message_request_enable_voice_success), userItem.getUsername());
                break;
            case VIDEO:
                message = String.format(context.getString(R.string.message_request_enable_video_success), userItem.getUsername());
                break;
            case VIDEO_CHAT:
                message = String.format(context.getString(R.string.message_request_enable_video_chat_success), userItem.getUsername());
                break;
            default:
                break;
        }
        return message;
    }

    private void onOutgoingCall(String callId) {
        UserItem callee = ConfigManager.getInstance().getCurrentCallee(callId);
        int callType = ConfigManager.getInstance().getCurrentCallType();
        switch (callType) {
            case Constant.API.VOICE_CALL:
                outgoingCallListener.outgoingVoiceCall(callee, callId);
                break;
            case Constant.API.VIDEO_CALL:
                outgoingCallListener.outgoingVideoCall(callee, callId);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                outgoingCallListener.outgoingVideoChatCall(callee, callId);
                break;
        }
    }

    private void reconnectRoom(String callId) {
        ReconnectCallTask reconnectCallTask = new ReconnectCallTask(context,
                callId);
        requestToServer(reconnectCallTask);
    }

    public interface OutgoingCallListener {

        void outgoingVoiceCall(UserItem callee, String callID);

        void outgoingVideoCall(UserItem callee, String callID);

        void outgoingVideoChatCall(UserItem callee, String callID);

        void didConnectCallError(int errorCode, String errorMessage);

        void onCalleeRejectCall(BusyCallEvent busyCallEvent);

        void didCheckCallError(int errorCode, String errorMessage);

        void didUserNotEnoughPoint(String title, String content, String positiveTitle);
    }
}
