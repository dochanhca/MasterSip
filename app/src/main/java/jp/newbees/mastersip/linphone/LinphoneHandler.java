package jp.newbees.mastersip.linphone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.view.SurfaceView;

import com.android.volley.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneFriendList;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.Reason;
import org.linphone.core.SubscriptionState;
import org.linphone.core.VideoSize;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.GSMCallEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CancelCallTask;
import jp.newbees.mastersip.network.api.SendDirectMessageTask;
import jp.newbees.mastersip.network.sip.base.PacketManager;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vietbq on 12/6/16.
 */

public class LinphoneHandler implements LinphoneCoreListener {
    private static final String TAG = "LinphoneHandler";
    private final AudioManager mAudioManager;
    private Context context;
    private boolean running;
    private LinphoneNotifier notifier;
    private LinphoneCore linphoneCore;

    private static LinphoneHandler instance;

    private String basePath;
    private String mRingSoundFile;
    private SipItem sipAccount;
    private LinphoneCall currentPausedCall;
    private boolean waitingGSMCall;
    private TimerTask timerWaitingCallTask;
    private static final long TIMEOUT = 45000;
    private static final long FIRST_SHOOT = 45000;
    private boolean cancelingCall;

    /**
     * @param notifier
     * @param context
     */
    private LinphoneHandler(LinphoneNotifier notifier, Context context) {
        this.notifier = notifier;
        this.context = context;
        mAudioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
    }

    public static final synchronized LinphoneHandler createAndStart(LinphoneNotifier notifier, Context context) {
        if (instance != null) {
            throw new RuntimeException("Linphone Handler is already initialized");
        }
        instance = new LinphoneHandler(notifier, context);

        return instance;
    }

    public static final synchronized boolean isRunning() {
        if (getInstance() == null) {
            return false;
        }
        return getInstance().running;
    }

    public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
        this.write(cfg.getIdentity() + " : " + state.toString());
        Logger.e(TAG, state.toString());
        RegisterVoIPManager.getInstance().registrationStateChanged(state, notifier);
    }

    public void show(LinphoneCore lc) {
    }

    public void byeReceived(LinphoneCore lc, String from) {
    }

    public void authInfoRequested(LinphoneCore lc, String realm, String username, String domain) {
    }

    @Override
    public void authenticationRequested(LinphoneCore linphoneCore, LinphoneAuthInfo linphoneAuthInfo, LinphoneCore.AuthMethod authMethod) {

    }

    public void displayStatus(LinphoneCore lc, String message) {
    }

    public void displayMessage(LinphoneCore lc, String message) {
    }

    public void displayWarning(LinphoneCore lc, String message) {
    }

    public void globalState(LinphoneCore lc, LinphoneCore.GlobalState state, String message) {
        Logger.e(TAG, message + " - " + state.toString());
        if (state == LinphoneCore.GlobalState.GlobalOn) {
            try {
                this.initLiblinphone(lc);
            } catch (LinphoneCoreException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {
    }

    public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
    }

    public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State cstate, String msg) {
        Logger.e(TAG, "CallState " + msg + " - " + cstate.toString() + " - " + cstate.value());
        int state = cstate.value();
        if (cstate == LinphoneCall.State.CallReleased
                || cstate == LinphoneCall.State.CallEnd
                || cstate == LinphoneCall.State.Error
                ) {
            resetDefaultSpeaker();
            notifyEndCallToServer();
        } else if (cstate == LinphoneCall.State.Pausing) {
            notifyPauseCallToServer();
        } else if (cstate == LinphoneCall.State.Resuming) {
            handleCallResuming();
        }
        String callId = ConfigManager.getInstance().getCallId();
        ReceivingCallEvent receivingCallEvent = new ReceivingCallEvent(state, callId);
        EventBus.getDefault().post(receivingCallEvent);
    }

    private void handleCallResuming() {
        this.currentPausedCall = null;
        this.waitingGSMCall = false;
        this.timerWaitingCallTask.cancel();
        this.notifyResumeCallToServer();
    }

    /**
     * update local ring with setting of ringtone in device
     */
    public void updateLocalRing() {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Logger.e(TAG, "AudioManager mode = " + audioManager.getRingerMode());
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                enableDeviceRingtone(false);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                enableDeviceRingtone(true);
                break;
            default:
                enableDeviceRingtone(true);
        }
        updateCall();
    }

    private void enableDeviceRingtone(boolean enable) {
        linphoneCore.setRing(enable ? mRingSoundFile : null);
    }

    private void resetDefaultSpeaker() {
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
    }

    public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {
    }

    public void ecCalibrationStatus(LinphoneCore lc, LinphoneCore.EcCalibratorStatus status, int delay_ms, Object data) {
    }

    public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted, String authenticationToken) {
    }

    public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event) {
    }

    public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {
    }

    /**
     * Gen SIP Address such as sip:1102@52.197.14.30
     *
     * @param extension 1102
     * @return full address sip
     */
    private String genSipAddressByExtension(String extension) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sip:").append(extension).append("@").append(ConfigManager.getInstance().getDomain());
        return stringBuilder.toString();
    }

    private void createLinphoneCore() {
        try {
            basePath = context.getFilesDir().getAbsolutePath();
            mRingSoundFile = basePath + "/oldphone_mono.wav";
            copyAssetsFromPackage(basePath);
            linphoneCore = LinphoneCoreFactory.instance().createLinphoneCore(this, basePath + "/.linphonerc", basePath + "/linphonerc", null, context);
            this.tryToLoginVoIP();
        } catch (LinphoneCoreException | IOException e) {
            e.printStackTrace();
        }
    }

    private void copyAssetsFromPackage(String basePath) throws IOException {
        LinphoneUtils.copyIfNotExist(context, R.raw.oldphone_mono, mRingSoundFile);
        LinphoneUtils.copyIfNotExist(context, R.raw.ringback, basePath + "/ringback.wav");
        LinphoneUtils.copyIfNotExist(context, R.raw.toy_mono, basePath + "/toy_mono.wav");
        LinphoneUtils.copyIfNotExist(context, R.raw.linphonerc_default, basePath + "/.linphonerc");
        LinphoneUtils.copyFromPackage(context, R.raw.linphonerc_factory, new File(basePath + "/linphonerc").getName());
        LinphoneUtils.copyIfNotExist(context, R.raw.lpconfig, basePath + "/lpconfig.xsd");
        LinphoneUtils.copyIfNotExist(context, R.raw.rootca, basePath + "/rootca.pem");
    }

    private void initLiblinphone(LinphoneCore lc) throws LinphoneCoreException {
        linphoneCore = lc;
        linphoneCore.setContext(context);
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL), 0);

        int availableCores = Runtime.getRuntime().availableProcessors();
        linphoneCore.setCpuCount(availableCores);

        setUserAgent();
        userFrontCamera(false);
        updateLocalRing();
        linphoneCore.setPlayLevel(30);
        linphoneCore.setVideoPreset("default");
        linphoneCore.setPreferredVideoSize(VideoSize.VIDEO_SIZE_VGA);
        linphoneCore.setPreferredFramerate(0);
        setBandwidthLimit(1024 + 128);
    }

    private void tryToLoginVoIP() {
        if (this.sipAccount != null) {
            this.loginVoIPServer(this.sipAccount);
        }
    }

    private void setBandwidthLimit(int bandwidth) {
        linphoneCore.setUploadBandwidth(bandwidth);
        linphoneCore.setDownloadBandwidth(bandwidth);
    }

    public static final synchronized LinphoneCore getLinphoneCore() {
        if (getInstance() == null) {
            return null;
        }
        return getInstance().linphoneCore;
    }

    public static final synchronized LinphoneHandler getInstance() {
        return instance;
    }

    public synchronized void loginVoIPServer(final SipItem sipItem) {
        try {
            this.sipAccount = sipItem;
            this.loginVoIPServer(
                    sipItem.getExtension(), sipItem.getSecret());
        } catch (LinphoneCoreException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public static final synchronized void restart(SipItem sipItem) {
        destroy();
        Logger.e(TAG, "Restart linphone...");
        getInstance().loginVoIPServer(sipItem);
    }

    public static synchronized void destroy() {
        Logger.e(TAG, "Shutting down linphone...");
        if (getInstance() == null) {
            return;
        }
        try {
            getInstance().setVideoWindow(null);
            getInstance().running = false;
            getInstance().linphoneCore.destroy();
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            instance = null;
        }
    }

    public void adjustVolume(int i) {
            // starting from ICS, volume must be adjusted by the application, at least for STREAM_VOICE_CALL volume stream
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, i < 0
                    ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * Login to VoIP Server (such as Asterisk, FreeSWITCH ...)
     *
     * @param extension extension
     * @param password  password
     * @throws LinphoneCoreException
     */
    private synchronized void loginVoIPServer(String extension, String password) throws LinphoneCoreException {
        String sipAddress = this.genSipAddressByExtension(extension);
        if (linphoneCore != null) {
            try {
                LinphoneCoreFactory lcFactory = LinphoneCoreFactory.instance();
                LinphoneAddress address = lcFactory.createLinphoneAddress(sipAddress);
                String username = address.getUserName();
                String domain = address.getDomain();
                address.setTransport(LinphoneAddress.TransportType.LinphoneTransportTcp);

                if (password != null) {
                    linphoneCore.addAuthInfo(lcFactory.createAuthInfo(username, password, (String) null, domain));
                }

                LinphoneProxyConfig proxyCfg = linphoneCore.createProxyConfig(sipAddress, address.asStringUriOnly(), address.asStringUriOnly(), true);
                linphoneCore.addProxyConfig(proxyCfg);
                linphoneCore.setDefaultProxyConfig(proxyCfg);
                linphoneCore.setNetworkReachable(true);
                this.running = true;
                while (this.running) {
                    linphoneCore.iterate();
                    this.sleep(20);
                }
            } catch (NullPointerException e) {
                this.running = false;
                Logger.e(TAG, "linphoneCore is " + linphoneCore);
            } finally {
                destroy();
            }
        } else {
            createLinphoneCore();
        }
    }

    private void setUserAgent() {
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (versionName == null) {
                versionName = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            }
            linphoneCore.setUserAgent("Android MasterSip", versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException var3) {
            Logger.e(TAG, "Interrupted!\nAborting");
        }
    }

    private void write(String s) {
        this.notifier.notify(s);
    }

    public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom chatRoom, LinphoneChatMessage message) {
        String raw = message.getText();
        PacketManager.getInstance().processData(raw);
    }

    public void transferState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State newCallState) {
    }

    public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {
    }

    public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev, SubscriptionState state) {
    }

    public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content) {
    }

    public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state) {
    }

    public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {
    }

    public void configuringStatus(LinphoneCore lc, LinphoneCore.RemoteProvisioningState state, String message) {
    }

    public void fileTransferProgressIndication(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, int progress) {
    }

    public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, byte[] buffer, int size) {
    }

    public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, ByteBuffer buffer, int size) {
        return 0;
    }

    public void uploadProgressIndication(LinphoneCore lc, int offset, int total) {
    }

    public void uploadStateChanged(LinphoneCore lc, LinphoneCore.LogCollectionUploadState state, String info) {
    }

    public void friendListCreated(LinphoneCore lc, LinphoneFriendList list) {
    }

    public void friendListRemoved(LinphoneCore lc, LinphoneFriendList list) {
    }

    public void sendPacket(String raw, String callee) {
        try {
            String addressSip = genSipAddressByExtension(callee);
            LinphoneAddress lAddress = linphoneCore.interpretUrl(addressSip);
            LinphoneChatRoom linphoneChatRoom = linphoneCore.getChatRoom(lAddress);
            linphoneChatRoom.sendMessage(raw);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }

    }

    public final void makeCall(int callType, String roomId) {
        ConfigManager.getInstance().setCurrentCallType(callType);
        switch (callType) {
            case Constant.API.VOICE_CALL:
                handleVoiceCall(roomId);
                break;
            case Constant.API.VIDEO_CALL:
                handleVideoVideoCall(roomId);
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                handleVideoChatCall(roomId);
                break;
            default:
                break;
        }
    }

    private void handleVoiceCall(String roomId) {
        enableSpeaker(false);
        muteMicrophone(false);
        call(roomId, false);
    }

    private void handleVideoVideoCall(String roomId) {
        enableSpeaker(false);
        muteMicrophone(false);
        userFrontCamera(false);
        call(roomId, true);
    }

    private void handleVideoChatCall(String roomId) {
        enableSpeaker(true);
        muteMicrophone(false);
        call(roomId, true);
    }

    public final void acceptCall() throws LinphoneCoreException {
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        linphoneCore.acceptCall(currentCall);
    }

    public final void terminalCall() {
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        if (currentCall != null) {
            linphoneCore.terminateCall(currentCall);
        }
    }

    public final void declineCall() {
        Logger.e(TAG, "Decline Call");
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        if (null != currentCall) {
            linphoneCore.declineCall(currentCall, Reason.Busy);
        }
    }

    /**
     * @param mute
     */
    public final void muteMicrophone(boolean mute) {
        linphoneCore.muteMic(mute);
    }

    /**
     * @param speaker
     */
    public final void enableSpeaker(boolean speaker) {
        linphoneCore.enableSpeaker(speaker);
    }

    /**
     * @param enable
     */
    public final void enableVideo(boolean enable) {
        LinphoneCall call = linphoneCore.getCurrentCall();
        call.enableCamera(enable);
        if (enable) {
            reInviteWithVideo();
        }
    }

    private void reInviteWithVideo() {
        LinphoneCall lCall = linphoneCore.getCurrentCall();
        if (lCall == null) {
            return;
        }
        LinphoneCallParams params = lCall.getCurrentParamsCopy();

        if (params.getVideoEnabled()) return;

        params.setVideoEnabled(true);
        params.setAudioBandwidth(0);

        // Abort if not enough bandwidth...
        if (!params.getVideoEnabled()) {
            return;
        }

        // Not yet in video call: try to re-invite with video
        linphoneCore.updateCall(lCall, params);
        return;
    }

    /**
     * switch camera and update call and preview window
     *
     * @param captureView
     */
    public void switchCamera(SurfaceView captureView) {
        try {
            switchCamera();
            updateCall();

            // previous call will cause graph reconstruction -> regive preview
            // window
            if (captureView != null) {
                linphoneCore.setPreviewWindow(captureView);
            }
        } catch (ArithmeticException ae) {
            Logger.e(TAG, "Cannot switch camera : no camera");
        }
    }

    public void userFrontCamera(boolean needUpdateCall) {
        int camId = 0;
        AndroidCameraConfiguration.AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
        for (AndroidCameraConfiguration.AndroidCamera androidCamera : cameras) {
            if (androidCamera.frontFacing)
                camId = androidCamera.id;
        }
        linphoneCore.setVideoDevice(camId);
        if (needUpdateCall) {
            updateCall();
        }
    }

    private void switchCamera() {
        int videoDeviceId = linphoneCore.getVideoDevice();
        videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
        linphoneCore.setVideoDevice(videoDeviceId);
    }

    private void updateCall() {
        LinphoneCall call = linphoneCore.getCurrentCall();
        if (call == null) {
            Logger.e(TAG, "Trying to updateCall while not in call: doing nothing");
            return;
        }
        linphoneCore.updateCall(call, null);
    }

    public final void call(String roomId, boolean enableVideo) {
        try {
            String addressSip = genSipAddressByExtension(roomId);
            LinphoneAddress lAddress = linphoneCore.interpretUrl(addressSip);
            LinphoneCallParams params = linphoneCore.createCallParams(null);
            params.setVideoEnabled(enableVideo);
            linphoneCore.inviteAddressWithParams(lAddress, params);
            Logger.e(TAG, "make a call to: " + addressSip);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    public final void sendReadMessageEvent(String fromExtension, String toExtension, BaseChatItem baseChatItem) {
        try {
            String raw = JSONUtils.genRawToChangeMessageState(baseChatItem, fromExtension);
            sendPacket(raw, toExtension);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean isCalling() {
        if (getInstance() == null) {
            return false;
        }
        return getInstance().linphoneCore.isIncall();
    }

    public void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        linphoneCore.setVideoWindow(androidVideoWindow);
    }

    public void setPreviewWindow(SurfaceView captureView) {
        linphoneCore.setPreviewWindow(captureView);
    }

    public final void pauseCurrentCall() {
        currentPausedCall = linphoneCore.getCurrentCall();
        if (currentPausedCall != null) {
            this.waitingGSMCall = true;
            this.linphoneCore.pauseCall(currentPausedCall);
        }
    }

    private void startTimerWaitingCall() {
        Timer timer = new Timer();
        timerWaitingCallTask = new TimerTask() {
            @Override
            public void run() {
                if (LinphoneHandler.this.waitingGSMCall) {
                    LinphoneHandler.this.waitingGSMCall = false;
                    LinphoneHandler.this.endWaitingCall();
                }
            }
        };
        timer.schedule(timerWaitingCallTask, FIRST_SHOOT, TIMEOUT);
    }

    private void endWaitingCall() {
        this.terminalCall();
    }

    public final void resumeCurrentCall() {
        if (currentPausedCall != null && this.waitingGSMCall) {
            this.waitingGSMCall = false;
            this.linphoneCore.resumeCall(currentPausedCall);
        }
    }

    public void handleIncomingCallGSM() {
        if (this.isCalling()) {
            pauseCurrentCall();
        }
    }

    public void handleIdleCallGSM() {
        if (this.waitingGSMCall) {
            this.resumeCurrentCall();
        }
    }

    public void handleOutgoingCallGSM() {

    }

    public void handleVolumeChanged(int volume) {
        linphoneCore.setPlayLevel(volume);
        updateCall();
    }

    private void notifyPauseCallToServer() {
        try {
            String roomId = ConfigManager.getInstance().getCallId();
            String toExtension = ConfigManager.getInstance().getCalleeByRoomId(roomId).getSipItem().getExtension();
            String message = genMessageGSM(GSMCallEvent.PAUSED_GSM_CALL_EVENT);
            Logger.e("LinphoneService", message);
            SendDirectMessageTask messageTask = new SendDirectMessageTask(getApplicationContext(), toExtension, message);
            messageTask.request(new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Logger.e(TAG, "Sent Pause call");
                    startTimerWaitingCall();
                }
            }, new BaseTask.ErrorListener() {
                @Override
                public void onError(int errorCode, String errorMessage) {
                    Logger.e(TAG, "Sent Pause call failure " + errorMessage);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void notifyResumeCallToServer() {
        try {
            String message = genMessageGSM(GSMCallEvent.RESUME_GSM_CALL_EVENT);
            String roomId = ConfigManager.getInstance().getCallId();
            String toExtension = ConfigManager.getInstance().getCalleeByRoomId(roomId).getSipItem().getExtension();
            SendDirectMessageTask messageTask = new SendDirectMessageTask(getApplicationContext(), toExtension, message);
            messageTask.request(new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Logger.e(TAG, "Sent Resume call");
                }
            }, null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private String genMessageGSM(int state) throws JSONException {
        String extension = ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension();
        JSONObject jMessage = new JSONObject();
        jMessage.put(Constant.JSON.ACTION, Constant.SOCKET.ACTION_GSM_CALL_STATE);
        jMessage.put(Constant.JSON.MESSAGE, "");

        JSONObject jResponse = new JSONObject();
        jResponse.put(Constant.JSON.EXTENSION, extension);
        jResponse.put(Constant.JSON.GSM_STATE, state);

        jMessage.put(Constant.JSON.RESPONSE, jResponse);
        return jMessage.toString();
    }

    private void notifyEndCallToServer() {
        String callId = ConfigManager.getInstance().getCallId();
        if (callId == null || cancelingCall) return;
        cancelingCall = true;
        CancelCallTask task = new CancelCallTask(getApplicationContext(), callId);
        task.request(new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                ConfigManager.getInstance().setCallId(null);
                cancelingCall = false;
                Logger.e("LinphoneService", "End call success ");
            }
        }, new BaseTask.ErrorListener() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                cancelingCall = false;
                Logger.e("LinphoneService", "End call error " + errorMessage);
            }
        });
    }
}
