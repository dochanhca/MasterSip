package jp.newbees.mastersip.linphone;

import android.content.Context;
import android.content.pm.PackageManager;

import org.greenrobot.eventbus.EventBus;
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
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.network.sip.base.PacketManager;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class LinphoneHandler implements LinphoneCoreListener {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private boolean running;
    private LinphoneNotifier notifier;
    private LinphoneCore linphoneCore;

    /**
     * @param notifier
     * @param context
     */
    public LinphoneHandler(LinphoneNotifier notifier, Context context) {
        this.notifier = notifier;
        this.context = context;
    }

    public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
        this.write(cfg.getIdentity() + " : " + state.toString());
        Logger.e("" + this + getClass().getSimpleName(), state.toString());
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
        String callerExtension = call.getChatRoom().getPeerAddress().getUserName();
        ReceivingCallEvent receivingCallEvent = new ReceivingCallEvent(state, callerExtension);
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
        linphoneCore.setPlayLevel(2);
        linphoneCore.enableSpeaker(true);
        linphoneCore.muteMic(false);
        linphoneCore.setPlayFile(basePath + "/toy_mono.wav");
        LinphoneCore.Transports transports = new LinphoneCore.Transports();
        transports.udp = 1;

        int availableCores = Runtime.getRuntime().availableProcessors();
        linphoneCore.setCpuCount(availableCores);
    }

    /**
     * Login to VoIP Server (such as Aterisk, FreeSWITCH ...)
     *
     * @param extension 10001
     * @param password  abcxzy
     * @throws LinphoneCoreException
     */
    public void loginVoIPServer(String extension, String password) throws LinphoneCoreException {
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

    public void stopMainLoop() {
        this.running = false;
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
     * @param video
     */
    public final void enableVideo(boolean video) {
        try {
            LinphoneCall linphoneCall = linphoneCore.getCurrentCall();
            LinphoneCallParams params = linphoneCore.createCallParams(linphoneCall);
            params.setVideoEnabled(video);
            linphoneCore.acceptCallUpdate(linphoneCall, params);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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

    public boolean isCalling() {
        return linphoneCore.isIncall();
    }
}
