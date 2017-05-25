package jp.newbees.mastersip.linphone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.view.SurfaceView;

import com.android.volley.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.CallDirection;
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
import org.linphone.core.PayloadType;
import org.linphone.core.PublishState;
import org.linphone.core.Reason;
import org.linphone.core.SubscriptionState;
import org.linphone.core.VideoSize;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_VOICE_CALL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vietbq on 12/6/16.
 */

public class LinphoneHandler implements LinphoneCoreListener {
    private static final String TAG = "LinphoneHandler";
    private static final int INCOMING_CALL_TIMEOUT = 60;
    private final AudioManager mAudioManager;
    private Context context;
    private LinphoneCore linphoneCore;

    private static LinphoneHandler instance;

    private String basePath;
    private String mRingSoundFile;
    private SipItem sipAccount;
    private LinphoneCall currentPausedCall;
    private boolean waitingGSMCall;
    private TimerTask timerWaitingCallTask;
    private static final long TIMEOUT = 15000;
    private static final long FIRST_SHOOT = 15000;
    private boolean cancelingCall;
    private LinphoneNotifier notifier;
    private boolean globalOff;
    private boolean stopLinphoneCore;
    private String mLinphoneRootCaFile;
    private String mUserCertificatePath;
    private Timer mTimer;
    private boolean mAudioFocused;

    /**
     * @param context
     */
    private LinphoneHandler(Context context, LinphoneNotifier notifier) {
        this.context = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.notifier = notifier;
    }

    public static final synchronized LinphoneHandler createAndStart(Context context, LinphoneNotifier notifier) {
        if (instance == null) {
            instance = new LinphoneHandler(context, notifier);
        }
        return instance;
    }

    public static final synchronized boolean isRunning() {
        if (instance != null) {
            return true;
        }
        return false;
    }

    public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
        Logger.e(TAG, state.toString());
        RegisterVoIPManager.getInstance().registrationStateChanged(state, notifier);
    }

    public void show(LinphoneCore lc) {
        // LinphoneCoreListener
    }

    public void byeReceived(LinphoneCore lc, String from) {
        // LinphoneCoreListener
    }

    public void authInfoRequested(LinphoneCore lc, String realm, String username, String domain) {
        // LinphoneCoreListener
    }

    @Override
    public void authenticationRequested(LinphoneCore linphoneCore, LinphoneAuthInfo linphoneAuthInfo, LinphoneCore.AuthMethod authMethod) {
        // LinphoneCoreListener
    }

    public void displayStatus(LinphoneCore lc, String message) {
        // LinphoneCoreListener
    }

    public void displayMessage(LinphoneCore lc, String message) {
        // LinphoneCoreListener
    }

    public void displayWarning(LinphoneCore lc, String message) {
        // LinphoneCoreListener
    }

    public void globalState(LinphoneCore lc, LinphoneCore.GlobalState state, String message) {
        Logger.e(TAG, message + " - " + state.toString());
        if (state == LinphoneCore.GlobalState.GlobalOn) {
            try {
                this.initLiblinphone();
            } catch (LinphoneCoreException e) {
                Logger.e(TAG, e.getMessage());
            }
        } else if (state == LinphoneCore.GlobalState.GlobalShutdown) {
            handleLinphoneShutdown();
        } else if (state == LinphoneCore.GlobalState.GlobalOff) {
            globalOff = true;
            removeInstance();
        }
    }

    private void handleLinphoneShutdown() {
        String callId = ConfigManager.getInstance().getCallId();
        ReceivingCallEvent receivingCallEvent = new ReceivingCallEvent(ReceivingCallEvent.LINPHONE_ERROR, callId);
        EventBus.getDefault().post(receivingCallEvent);
    }

    public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {
        // LinphoneCoreListener
    }

    public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
        // LinphoneCoreListener
    }

    public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State cstate, String msg) {
        Logger.e(TAG, "CallState " + msg + " - " + cstate.toString() + " - " + cstate.value());
        int state = cstate.value();
        String callId = ConfigManager.getInstance().getCallId();
        if (cstate == LinphoneCall.State.CallReleased) {
            checkEndWhileWaitingCall();
        } else if (cstate == LinphoneCall.State.CallEnd) {
            resetDefaultSpeaker();
        } else if (cstate == LinphoneCall.State.Pausing) {
            notifyPauseCallToServer();
        } else if (cstate == LinphoneCall.State.Resuming) {
            handleCallResuming();
        } else if (cstate == LinphoneCall.State.IncomingReceived || (cstate == LinphoneCall.State.CallIncomingEarlyMedia)) {
            if (linphoneCore.getCallsNb() == 1) {
                requestAudioFocus(STREAM_RING);
            }
        } else if (cstate == LinphoneCall.State.Connected && call.getDirection() == CallDirection.Incoming) {
            mAudioManager.abandonAudioFocus(null);
            requestAudioFocus(STREAM_VOICE_CALL);
        }

        if ((cstate == LinphoneCall.State.Connected ||
                cstate == LinphoneCall.State.StreamsRunning) &&
                call.getDirection() == CallDirection.Incoming) {
            checkCallConnected(cstate, callId);
        }

        ReceivingCallEvent receivingCallEvent = new ReceivingCallEvent(state, call.getDirection(), callId);
        EventBus.getDefault().post(receivingCallEvent);
    }

    private void checkCallConnected(LinphoneCall.State state, String callId) {
        int callType = ConfigManager.getInstance().getCurrentCallType();
        if (callType == Constant.API.VOICE_CALL
                && state == LinphoneCall.State.Connected) {
            ConfigManager.getInstance().setCallState(callId, ConfigManager.CALL_STATE_CONNECTED);
        } else if ((callType == Constant.API.VIDEO_CALL || callType == Constant.API.VIDEO_CHAT_CALL)
                && state == LinphoneCall.State.StreamsRunning) {
            ConfigManager.getInstance().setCallState(callId, ConfigManager.CALL_STATE_CONNECTED);
        }
    }

    private void checkEndWhileWaitingCall() {
        String callId = ConfigManager.getInstance().getCallId();
        int callState = ConfigManager.getInstance().getCallState(callId);
        if (callState == ConfigManager.CALL_STATE_WAITING) {
            notifyEndCall(callId);
        }
    }

    private void notifyEndCall(String callId) {
        CancelCallTask cancelCallTask = new CancelCallTask(getContext(), callId);
        cancelCallTask.request(new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                ConfigManager.getInstance().removeCurrentCall();
                Logger.e("LinphoneHandler", "End waiting call");
            }
        }, new BaseTask.ErrorListener() {
            @Override
            public void onError(int errorCode, String errorMessage) {
                Logger.e("LinphoneHandler", "End waiting call failed");
            }
        });
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
        if (linphoneCore != null) {
            linphoneCore.setRing(enable ? mRingSoundFile : null);
        }
    }

    private void resetDefaultSpeaker() {
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
    }

    public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {
        // LinphoneCoreListener
    }

    public void ecCalibrationStatus(LinphoneCore lc, LinphoneCore.EcCalibratorStatus status, int delay_ms, Object data) {
        // LinphoneCoreListener
    }

    public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted, String authenticationToken) {
        // LinphoneCoreListener
    }

    public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event) {
        // LinphoneCoreListener
    }

    public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {
        // LinphoneCoreListener
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
            Logger.e(TAG, "Create a new linphone core");
            basePath = context.getFilesDir().getAbsolutePath();
            mLinphoneRootCaFile = basePath + "/rootca.pem";
            mUserCertificatePath = basePath;
            mRingSoundFile = basePath + "/oldphone_mono.wav";
            copyAssetsFromPackage(basePath);
            linphoneCore = LinphoneCoreFactory.instance().createLinphoneCore(this, basePath + "/.linphonerc", basePath + "/linphonerc", null, context);
            TimerTask lTask = new TimerTask() {
                @Override
                public void run() {
                    UIThreadDispatcher.dispatch(new Runnable() {
                        @Override
                        public void run() {
                            if (linphoneCore != null) {
                                linphoneCore.iterate();
                            }
                        }
                    });
                }
            };
            mTimer = new Timer("Linphone scheduler");
            mTimer.schedule(lTask, 0, 20);
        } catch (LinphoneCoreException | IOException | NullPointerException e) {
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
        copyFromPackage(R.raw.rootca, new File(mLinphoneRootCaFile).getName());
    }

    public void copyFromPackage(int ressourceId, String target) throws IOException {
        FileOutputStream lOutputStream = context.openFileOutput(target, 0);
        InputStream lInputStream = context.getResources().openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

    private void initLiblinphone() throws LinphoneCoreException {
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
        linphoneCore.setIncomingTimeout(INCOMING_CALL_TIMEOUT);
        mAudioManager.setStreamVolume(STREAM_VOICE_CALL,
                mAudioManager.getStreamVolume(STREAM_VOICE_CALL), 0);

        int availableCores = Runtime.getRuntime().availableProcessors();
        linphoneCore.setCpuCount(availableCores);
        linphoneCore.enableEchoCancellation(true);
        linphoneCore.enableKeepAlive(true);
        linphoneCore.enableIpv6(true);

        setUserAgent();
        useFrontCamera();
        updateLocalRing();
        linphoneCore.setVideoPreset("30");
        linphoneCore.setPreferredVideoSize(VideoSize.VIDEO_SIZE_VGA);
        linphoneCore.setPreferredFramerate(0);
        linphoneCore.setRootCA(mLinphoneRootCaFile);
        linphoneCore.setUserCertificatesPath(mUserCertificatePath);
        setBandwidthLimit(1024 + 128);
        linphoneCore.enableIpv6(true);
        supportOnlyH264();
        linphoneCore.setNetworkReachable(true);
        this.tryToLoginVoIP();
    }

    private void supportOnlyH264() throws LinphoneCoreException {
        PayloadType[] videoCodecs = linphoneCore.getVideoCodecs();
        for (PayloadType payloadType : videoCodecs) {
            if ("H264".equals(payloadType.getMime())) {
                linphoneCore.enablePayloadType(payloadType, true);
            } else {
                linphoneCore.enablePayloadType(payloadType, false);
            }
        }
        H264Helper.setH264Mode(H264Helper.MODE_AUTO, linphoneCore);
    }

    private void tryToLoginVoIP() throws LinphoneCoreException {
        if (this.sipAccount != null) {
            loginVoIPServer(this.sipAccount.getExtension(), this.sipAccount.getSecret());
        }
    }

    private void setBandwidthLimit(int bandwidth) {
        linphoneCore.setUploadBandwidth(bandwidth);
        linphoneCore.setDownloadBandwidth(bandwidth);
    }

    public static final synchronized LinphoneHandler getInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    public synchronized void loginVoIPServer(final SipItem sipItem) {
        try {
            Logger.e(TAG, "Logging " + sipItem.getExtension() + " - " + sipItem.getSecret());
            LinphoneHandler.this.sipAccount = sipItem;
            LinphoneHandler.this.loginVoIPServer(
                    sipItem.getExtension(), sipItem.getSecret());
        } catch (LinphoneCoreException e) {
            Logger.e(TAG, e.getMessage());
        } catch (NullPointerException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public static synchronized void destroy() {
        LinphoneHandler.getInstance().clearAll();
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
        String identity = this.genSipAddressByExtension(extension);
        if (linphoneCore != null) {
            linphoneCore.clearAuthInfos();
            linphoneCore.clearProxyConfigs();
            String domain = ConfigManager.getInstance().getDomain();
            String proxy = "sip:" + domain;
            LinphoneAddress identityAddress = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
            LinphoneAddress proxyAddress = LinphoneCoreFactory.instance().createLinphoneAddress(proxy);
            proxyAddress.setTransport(LinphoneAddress.TransportType.LinphoneTransportTcp);
            LinphoneProxyConfig
                    prxCfg = linphoneCore.createProxyConfig(identityAddress.asString(), proxyAddress.asStringUriOnly(), null, true);
            prxCfg.enableAvpf(false);
            prxCfg.setAvpfRRInterval(0);
            prxCfg.enableQualityReporting(false);
            prxCfg.setQualityReportingCollector(null);
            prxCfg.setQualityReportingInterval(0);
            linphoneCore.addProxyConfig(prxCfg);

            LinphoneAuthInfo authInfo = LinphoneCoreFactory.instance().createAuthInfo(extension, null, password, null, null, domain);
            linphoneCore.addAuthInfo(authInfo);
            linphoneCore.setDefaultProxyConfig(prxCfg);
        } else {
            createLinphoneCore();
        }
    }

    private void clearAll() {
        try {
            Logger.e(TAG, "Clear all proxy and auth");
            getInstance().mTimer.cancel();
            getInstance().terminalCall();
            getInstance().linphoneCore.clearAuthInfos();
            getInstance().linphoneCore.clearProxyConfigs();
            getInstance().linphoneCore.destroy();
            getInstance().context = null;
            stopLinphoneCore = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            removeInstance();
        }
    }

    private synchronized void removeInstance() {
        if (stopLinphoneCore && globalOff) {
            instance.linphoneCore = null;
            instance = null;
            Logger.e(TAG, "Removed instance");
        }
    }

    private void tryToRemoveLastAuthInfo() {
        LinphoneAuthInfo[] authInfos = linphoneCore.getAuthInfosList();
        if (authInfos != null) {
            Logger.e(TAG, "trying to remove AuthInfo");
            for (LinphoneAuthInfo linphoneAuthInfo : authInfos) {
                linphoneCore.removeAuthInfo(linphoneAuthInfo);
            }
        }
    }

    private void tryToRemoveLastProxyConfig() {
        LinphoneProxyConfig[] configList = linphoneCore.getProxyConfigList();
        if (configList != null) {
            Logger.e(TAG, "trying to remove ProxyConfig");
            for (LinphoneProxyConfig proxyConfig : configList) {
                linphoneCore.removeProxyConfig(proxyConfig);
            }
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

    public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom chatRoom, LinphoneChatMessage message) {
        String raw = message.getText();
        PacketManager.getInstance().processData(raw);
    }

    @Override
    public void messageReceivedUnableToDecrypted(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage) {

    }

    public void transferState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State newCallState) {
        // LinphoneCoreListener
    }

    public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {
        // LinphoneCoreListener
    }

    public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev, SubscriptionState state) {
        // LinphoneCoreListener
    }

    public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content) {
        // LinphoneCoreListener
    }

    public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state) {
        // LinphoneCoreListener
    }

    public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {
        // LinphoneCoreListener
    }

    public void configuringStatus(LinphoneCore lc, LinphoneCore.RemoteProvisioningState state, String message) {
        // LinphoneCoreListener
    }

    public void fileTransferProgressIndication(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, int progress) {
        // LinphoneCoreListener
    }

    public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, byte[] buffer, int size) {
        // LinphoneCoreListener
    }

    public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message, LinphoneContent content, ByteBuffer buffer, int size) {
        return 0;
    }

    public void uploadProgressIndication(LinphoneCore lc, int offset, int total) {
        // LinphoneCoreListener
    }

    public void uploadStateChanged(LinphoneCore lc, LinphoneCore.LogCollectionUploadState state, String info) {
        // LinphoneCoreListener
    }

    public void friendListCreated(LinphoneCore lc, LinphoneFriendList list) {
        // LinphoneCoreListener
    }

    public void friendListRemoved(LinphoneCore lc, LinphoneFriendList list) {
        // LinphoneCoreListener
    }

    @Override
    public void networkReachableChanged(LinphoneCore linphoneCore, boolean b) {
        // LinphoneCoreListener
    }

    public void sendPacket(String raw, String callee) {
        try {
            String addressSip = genSipAddressByExtension(callee);
            LinphoneAddress lAddress = linphoneCore.interpretUrl(addressSip);
            LinphoneChatRoom linphoneChatRoom = linphoneCore.getChatRoom(lAddress);
            linphoneChatRoom.sendMessage(raw);
            Logger.e(TAG, raw);
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
        enableMic(false);
        call(roomId, false);
    }

    private void handleVideoVideoCall(String roomId) {
        enableSpeaker(false);
        enableMic(false);
        useFrontCamera();
        call(roomId, true);
    }

    private void handleVideoChatCall(String roomId) {
        enableSpeaker(false);
        enableMic(false);
        useFrontCamera();
        call(roomId, true);
    }

    public final void acceptCall(boolean video) throws LinphoneCoreException {
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        LinphoneCallParams params = linphoneCore.createCallParams(currentCall);
        params.enableLowBandwidth(false);
        params.setVideoEnabled(video);
        linphoneCore.acceptCallWithParams(currentCall, params);
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

    public final boolean isSpeakerEnabled() {
        return linphoneCore.isSpeakerEnabled();
    }

    public final boolean isMicEnabled() {
        return !linphoneCore.isMicMuted();
    }

    /**
     * @param enable
     */
    public final void enableMic(boolean enable) {
        linphoneCore.muteMic(!enable);
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
        if (call == null) {
            return;
        }
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

    private void useFrontCamera(boolean needUpdateCall) {
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

    public void useFrontCamera() {
        useFrontCamera(false);
    }

    public void useFrontCameraAndUpdateCall() {
        useFrontCamera(true);
    }

    private void switchCamera() {
        int videoDeviceId = linphoneCore.getVideoDevice();
        videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
        linphoneCore.setVideoDevice(videoDeviceId);
    }

    private void updateCall() {
        if (LinphoneHandler.isRunning()) {
            LinphoneCall call = linphoneCore.getCurrentCall();
            if (call == null) {
                Logger.e(TAG, "Trying to updateCall while not in call: doing nothing");
                return;
            }
            linphoneCore.updateCall(call, null);
        }
    }

    public final void call(String roomId, boolean enableVideo) {
        try {
            String addressSip = genSipAddressByExtension(roomId);
            LinphoneAddress lAddress = linphoneCore.interpretUrl(addressSip);
            LinphoneCallParams params = linphoneCore.createCallParams(null);
            params.setVideoEnabled(enableVideo);
            params.setAudioBandwidth(0);
            params.enableLowBandwidth(false);
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
        if (instance == null) {
            return false;
        }
        return getInstance().linphoneCore.isIncall();
    }

    public void setVideoWindow(Object androidVideoWindow) {
        linphoneCore.setVideoWindow(androidVideoWindow);
    }

    public void zoomVideo(float zoomFactor, float zoomCenterX, float zoomCenterY) {
        LinphoneCall linphoneCall = linphoneCore.getCurrentCall();
        if (linphoneCall != null) {
            linphoneCall.zoomVideo(zoomFactor, zoomCenterX, zoomCenterY);
            updateCall();
        }
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
                    LinphoneHandler.this.endWaitingCall();
                    timerWaitingCallTask.cancel();
                }
            }
        };
        timer.schedule(timerWaitingCallTask, FIRST_SHOOT, TIMEOUT);
    }

    private void endWaitingCall() {
        this.linphoneCore.terminateCall(currentPausedCall);
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
        //LinphoneCoreListener
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
        } catch (JSONException | NullPointerException e) {
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
        } catch (JSONException | NullPointerException e) {
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

    public Context getContext() {
        return context;
    }

    private void requestAudioFocus(int stream) {
        if (!mAudioFocused) {
            int res = mAudioManager.requestAudioFocus(null, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            Log.d("Audio focus requested: " + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "Granted" : "Denied"));
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mAudioFocused = true;
        }
    }

    public boolean enableDownloadOpenH264() {
        return linphoneCore.downloadOpenH264Enabled();
    }

    public void reloadMsPlugins(String nativeLibraryDir) {
        linphoneCore.reloadMsPlugins(nativeLibraryDir);
    }
}
