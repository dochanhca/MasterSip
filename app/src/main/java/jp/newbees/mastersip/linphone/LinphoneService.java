package jp.newbees.mastersip.linphone;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.event.call.CallEvent;
import jp.newbees.mastersip.event.call.FlashedEvent;
import jp.newbees.mastersip.event.call.MicrophoneEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.eventbus.SendingReadMessageEvent;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service {

    private LinphoneHandler linphoneHandler;
    private final String TAG = "LinphoneService";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG, "onCreate");
        EventBus.getDefault().register(this);
        Handler mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        linphoneHandler = new LinphoneHandler(notifier, this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG, "OnStartCommand");
        SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
        loginToVoIP(sipItem);
        return super.onStartCommand(intent, flags, startId);
    }

    private void loginToVoIP(final SipItem sipItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.e("LinephonService", "Logging " + sipItem.getExtension() + " - " + sipItem.getSecret());
                    linphoneHandler.loginVoIPServer(
                            sipItem.getExtension(), sipItem.getSecret());
                } catch (LinphoneCoreException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        linphoneHandler.stopMainLoop();
        Logger.e(TAG, "Stop Linphone Service");
    }

    /**
     * This method invoked by EventBus when user accept or reject a call
     *
     * @param acceptCallEvent
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSendingCallEvent(SendingCallEvent acceptCallEvent) {
        switch (acceptCallEvent.getEvent()) {
            case SendingCallEvent.ACCEPT_CALL:
                handleAcceptCall();
                break;
            case SendingCallEvent.REJECT_CALL:
                handleRejectCall();
                break;
            case SendingCallEvent.END_CALL:
                handleEndCall();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public final void onCheckFlashedCall(FlashedEvent flashedEvent) {
        boolean calling = linphoneHandler.isCalling();
        if (!calling) {
            handleFlashedCall();
        }
    }

    private void handleFlashedCall() {
        EventBus.getDefault().post(new ReceivingCallEvent(ReceivingCallEvent.FLASHED_CALL));
    }

    /**
     * This method invoked by EventBus when enable or disable Speaker
     *
     * @param speakerEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public final void onSpeakerEvent(SpeakerEvent speakerEvent) {
        linphoneHandler.enableSpeaker(speakerEvent.isEnable());
    }

    /**
     * This method invoked by EventBus when enable or disable Mic
     *
     * @param microphoneEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public final void onMicrophoneEvent(MicrophoneEvent microphoneEvent) {
        linphoneHandler.muteMicrophone(microphoneEvent.isMute());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public final void onCallEvent(CallEvent callEvent) {
        int callType = callEvent.getCallType();
        ConfigManager.getInstance().setCurrentCallType(callType);
        switch (callType) {
            case Constant.API.VOICE_CALL:
                handleVoiceCall(callEvent.getCallId());
                break;
            case Constant.API.VIDEO_CALL:
                handleVideoVideoCall(callEvent.getCallId());
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                handleVideoChatCall(callEvent.getCallId());
                break;
            default:
                break;
        }
    }

    private void handleVoiceCall(String roomId) {
        linphoneHandler.enableSpeaker(false);
        linphoneHandler.muteMicrophone(false);
        linphoneHandler.call(roomId, false);
    }

    private void handleVideoVideoCall(String callee) {
        linphoneHandler.enableSpeaker(true);
        linphoneHandler.muteMicrophone(false);
        linphoneHandler.call(callee,true);
    }

    private void handleVideoChatCall(String callee) {
        linphoneHandler.enableSpeaker(true);
        linphoneHandler.muteMicrophone(false);
        linphoneHandler.call(callee,true);
    }

    /**
     * End current call
     */
    private void handleEndCall() {
        linphoneHandler.endCall();
    }

    /**
     * Reject a incoming call
     */
    private void handleRejectCall() {
        linphoneHandler.rejectCall();
    }

    /**
     * Accept a incoming call
     */
    private void handleAcceptCall() {
        try {
            linphoneHandler.acceptCall();
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public final void onSendingReadMessageEvent(SendingReadMessageEvent sendingReadMessageEvent) {
        try {
            String fromExtension = sendingReadMessageEvent.getCurrentUser().getSipItem().getExtension();
            String toExtension = sendingReadMessageEvent.getReplyUser().getSipItem().getExtension();
            String raw = JSONUtils.genRawToChangeMessageState(sendingReadMessageEvent.getBaseChatItem(), fromExtension);
            linphoneHandler.sendPacket(raw, toExtension);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
