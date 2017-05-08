package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.linphone.core.OpenH264DownloadHelperListener;
import org.linphone.mediastream.Version;
import org.linphone.tools.OpenH264DownloadHelper;

import java.util.Map;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckIncomingCallTask;
import jp.newbees.mastersip.network.api.ReconnectCallTask;
import jp.newbees.mastersip.network.api.UpdateCallWhenOnlineTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by thangit14 on 3/29/17.
 */

public class LinphoneServicePresenter extends BasePresenter {
    private final OpenH264DownloadHelper mCodecDownloader;
    private IncomingCallListener incomingCallListener;
    private OpenH264DownloadHelperListener h264DownloadHelperListener;

    public LinphoneServicePresenter(Context context, IncomingCallListener incomingCallListener, OpenH264DownloadHelperListener h264DownloadHelperListener) {
        super(context);
        this.incomingCallListener = incomingCallListener;
        this.mCodecDownloader = new OpenH264DownloadHelper(context);
        this.h264DownloadHelperListener = h264DownloadHelperListener;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof CheckIncomingCallTask) {
            Map<String, Object> result = (Map<String, Object>) task.getDataResponse();
            int callType = (int) result.get(CheckIncomingCallTask.INCOMING_CALL_TYPE);
            UserItem caller = (UserItem) result.get(CheckIncomingCallTask.CALLER);
            String callID = (String) result.get(CheckIncomingCallTask.CALL_ID);
            ConfigManager.getInstance().setCurrentCallUser(caller, callID);
            ConfigManager.getInstance().setCallState(callID, ConfigManager.CALL_STATE_WAITING);
            handleIncomingCallType(callType, caller, callID);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof CheckIncomingCallTask) {
            incomingCallListener.didCheckCallError(errorCode, errorMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(ReceivingCallEvent receivingCallEvent) {
        if (receivingCallEvent.getCallEvent() == ReceivingCallEvent.INCOMING_CALL) {
            onIncomingCall();
        }
    }

    /**
     * @param event listener Register VoIP response
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterVoIPEvent(RegisterVoIPEvent event) {
        Logger.e(tag, "onRegisterVoIPEvent receive: " + event.getResponseCode());
        if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_SUCCESS) {
            this.notifyToServer();
            if (!MyLifecycleHandler.getInstance().isApplicationVisible()) {
                saveLoginState(true);
                reconnectRoom(ConfigManager.getInstance().getCallId());
            }
            checkDownloadOpenH264();
        } else if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_FAILED){
            stopLinphoneService();
        }
    }

    private final void checkDownloadOpenH264() {
        if (Version.getCpuAbis().contains("armeabi-v7a")
                && !Version.getCpuAbis().contains("x86")
                && !mCodecDownloader.isCodecFound()
                && LinphoneHandler.getInstance().enableDownloadOpenH264()
                ) {
            Logger.e("LinphoneServicePresenter", "We will download OpenH264");
            mCodecDownloader.setOpenH264HelperListener(h264DownloadHelperListener);
            mCodecDownloader.downloadCodec();
        }else {
            Logger.e("LinphoneServicePresenter", "No need download OpenH264");
        }
    }

    private void notifyToServer() {
        if (!LinphoneHandler.getInstance().isCalling() &&
                ConfigManager.getInstance().getStartServiceFrom() == LinphoneService.START_FROM_ACTIVITY) {
            UpdateCallWhenOnlineTask task = new UpdateCallWhenOnlineTask(context);
            requestToServer(task);
        }
    }

    private void reconnectRoom(String callId) {
        ReconnectCallTask reconnectCallTask = new ReconnectCallTask(context, callId);
        requestToServer(reconnectCallTask);
    }

    private void stopLinphoneService() {
        LinphoneService.stopLinphone(context);
    }

    private void saveLoginState(boolean loginState) {
        ConfigManager.getInstance().saveLoginFlag(loginState);
    }

    private void handleIncomingCallType(int callType, UserItem caller, String callID) {
        if (MyLifecycleHandler.getInstance().isApplicationVisible()) {
            ReceivingCallEvent event = new ReceivingCallEvent(this.getEventCall(callType), caller, callID);
            EventBus.getDefault().post(event);
        } else {
            handleIncomingCallFromBackground(callType, caller, callID);
        }
    }

    private void handleIncomingCallFromBackground(int callType, UserItem caller, String callID) {
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

    private int getEventCall(int callType) {
        switch (callType) {
            case Constant.API.VOICE_CALL:
                return ReceivingCallEvent.CHECKED_INCOMING_VOICE_CALL;
            case Constant.API.VIDEO_CALL:
                return ReceivingCallEvent.CHECKED_INCOMING_VIDEO_CALL;
            default:
                return ReceivingCallEvent.CHECKED_INCOMING_VIDEO_CHAT_CALL;
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
