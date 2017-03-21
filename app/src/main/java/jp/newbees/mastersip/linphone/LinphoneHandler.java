package jp.newbees.mastersip.linphone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.SurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
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

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.network.sip.base.PacketManager;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class LinphoneHandler implements LinphoneCoreListener {
    private static final String TAG = "LinphoneHandler";
    private Context context;
    private boolean running;
    private LinphoneNotifier notifier;
    private LinphoneCore linphoneCore;

    private static LinphoneHandler instance;

    private Timer mTimer;

    /**
     * @param notifier
     * @param context
     */
    private LinphoneHandler(LinphoneNotifier notifier, Context context) {
        this.notifier = notifier;
        this.context = context;
    }

    public synchronized static final LinphoneHandler createAndStart(LinphoneNotifier notifier, Context context) {
        if (instance != null) {
            throw new RuntimeException("Linphone Handler is already initialized");
        }
        instance = new LinphoneHandler(notifier, context);

        return instance;
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
        //NOTE 2
    }

    public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {
    }

    public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
    }

    public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State cstate, String msg) {
        Logger.e(TAG, msg + " - " + cstate.toString());
        int state = cstate.value();
        if (cstate == LinphoneCall.State.CallReleased || cstate == LinphoneCall.State.CallEnd) {
            resetDefaultSpeaker();
        }
        String callId = ConfigManager.getInstance().getCallId();
        ReceivingCallEvent receivingCallEvent = new ReceivingCallEvent(state, callId);
        EventBus.getDefault().post(receivingCallEvent);
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
            String basePath = context.getFilesDir().getAbsolutePath();
            copyAssetsFromPackage(basePath);
            linphoneCore = LinphoneCoreFactory.instance().createLinphoneCore(this, basePath + "/.linphonerc", basePath + "/linphonerc", null, context);
            initLinphoneCoreValues(basePath);
            setUserAgent();
            setFrontCamAsDefault();
            linphoneCore.setNetworkReachable(true); // Let's assume it's true
        } catch (LinphoneCoreException | IOException e) {
            e.printStackTrace();
        }
    }

    private void copyAssetsFromPackage(String basePath) throws IOException {
        LinphoneUtils.copyIfNotExist(context, R.raw.oldphone_mono, basePath + "/oldphone_mono.wav");
        LinphoneUtils.copyIfNotExist(context, R.raw.ringback, basePath + "/ringback.wav");
        LinphoneUtils.copyIfNotExist(context, R.raw.toy_mono, basePath + "/toy_mono.wav");
        LinphoneUtils.copyIfNotExist(context, R.raw.linphonerc_default, basePath + "/.linphonerc");
        LinphoneUtils.copyFromPackage(context, R.raw.linphonerc_factory, new File(basePath + "/linphonerc").getName());
        LinphoneUtils.copyIfNotExist(context, R.raw.lpconfig, basePath + "/lpconfig.xsd");
        LinphoneUtils.copyIfNotExist(context, R.raw.rootca, basePath + "/rootca.pem");
    }

    private void initLinphoneCoreValues(String basePath) {
        linphoneCore.setContext(context);
        linphoneCore.setRing(null);
        linphoneCore.setPlayLevel(0);
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
        linphoneCore.setPlayFile(basePath + "/toy_mono.wav");

        int availableCores = Runtime.getRuntime().availableProcessors();
        linphoneCore.setCpuCount(availableCores);
    }


    public static synchronized final LinphoneCore getLinphoneCore() {
        return getInstance().linphoneCore;
    }

    public static synchronized final LinphoneHandler getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new RuntimeException("Linphone Manager should be created before accessed");
    }

    public synchronized void loginVoIPServer(final SipItem sipItem) {
        try {
            loginVoIPServer(
                    sipItem.getExtension(), sipItem.getSecret());
        } catch (LinphoneCoreException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public static synchronized final void restart(SipItem sipItem) {
        destroy();
        getInstance().loginVoIPServer(sipItem);
    }

    public static synchronized void destroy() {
        if (getInstance() == null) {
            return;
        }
        try {
            getInstance().running = false;
            getInstance().mTimer.cancel();
            getInstance().linphoneCore.destroy();
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
        }
    }


    /**
     * Login to VoIP Server (such as Aterisk, FreeSWITCH ...)
     *
     * @param extension 10001
     * @param password  abcxzy
     * @throws LinphoneCoreException
     */
    private synchronized void loginVoIPServer(String extension, String password) throws LinphoneCoreException {
        String sipAddress = this.genSipAddressByExtension(extension);
        LinphoneCoreFactory lcFactory = LinphoneCoreFactory.instance();
        createLinphoneCore();
        try {
            LinphoneAddress address = lcFactory.createLinphoneAddress(sipAddress);
            String username = address.getUserName();
            String domain = address.getDomain();
            address.setTransport(LinphoneAddress.TransportType.LinphoneTransportTcp);

            if (password != null) {
                linphoneCore.addAuthInfo(lcFactory.createAuthInfo(username, password, (String) null, domain));
            }

            LinphoneProxyConfig proxyCfg = linphoneCore.createProxyConfig(sipAddress, address.asStringUriOnly(), address.asStringUriOnly(), true);
            proxyCfg.setExpires(2000);
            linphoneCore.addProxyConfig(proxyCfg);
            linphoneCore.setDefaultProxyConfig(proxyCfg);
            this.running = true;

            while (this.running) {
                linphoneCore.iterate();
                this.sleep(50);
            }

            linphoneCore.getDefaultProxyConfig().edit();
            linphoneCore.getDefaultProxyConfig().enableRegister(false);
            linphoneCore.getDefaultProxyConfig().done();

        } catch (NullPointerException e) {
            Logger.e(TAG, "linphoneCore is " + linphoneCore);
        } finally {
            Logger.e(TAG, "Shutting down linphone...");
            linphoneCore.destroy();
        }
    }

    private void setFrontCamAsDefault() {
        int camId = 0;
        AndroidCameraConfiguration.AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
        for (AndroidCameraConfiguration.AndroidCamera androidCamera : cameras) {
            if (androidCamera.frontFacing)
                camId = androidCamera.id;
        }
        linphoneCore.setVideoDevice(camId);
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

    public void transferState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State new_call_state) {
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

    public final void endCall() {
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        linphoneCore.terminateCall(currentCall);
    }

    public final void rejectCall() {
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
        linphoneCore.setPreferredVideoSize(new VideoSize(1080, 1920));
        call.enableCamera(enable);
        reinviteWithVideo();
    }

    private boolean reinviteWithVideo() {
        LinphoneCall lCall = linphoneCore.getCurrentCall();
        if (lCall == null) {
            return false;
        }
        LinphoneCallParams params = lCall.getCurrentParamsCopy();

        if (params.getVideoEnabled()) return false;

        params.setVideoEnabled(true);
        params.setAudioBandwidth(0);
        userFrontCamera();

        // Abort if not enough bandwidth...
        if (!params.getVideoEnabled()) {
            return false;
        }

        // Not yet in video call: try to re-invite with video
        linphoneCore.updateCall(lCall, params);
        return true;
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

    public void userFrontCamera() {
        if (AndroidCameraConfiguration.hasFrontCamera()) {
            linphoneCore.setVideoDevice(1);
        } else {
            Logger.e(TAG, "Cannot use front camera : no camera");
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

    public boolean isCalling() {
        return linphoneCore.isIncall();
    }

    public void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        linphoneCore.setVideoWindow(androidVideoWindow);
    }

    public void setPreviewWindow(SurfaceView captureView) {
        linphoneCore.setPreviewWindow(captureView);
    }
}
