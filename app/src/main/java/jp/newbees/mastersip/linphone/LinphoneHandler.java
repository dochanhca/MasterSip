package jp.newbees.mastersip.linphone;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
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
import org.linphone.core.SubscriptionState;

import java.nio.ByteBuffer;

import jp.newbees.mastersip.eventbus.CallEvent;
import jp.newbees.mastersip.network.sip.base.PacketManager;
import jp.newbees.mastersip.utils.ConfigManager;
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

    public LinphoneHandler(LinphoneNotifier notifier, Context context) {
        this.notifier = notifier;
        this.context = context;
    }


    public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
        this.write(cfg.getIdentity() + " : " + state.toString());
        Logger.e(getClass().getSimpleName(), state.toString());
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
        Logger.e(TAG,msg);
        int state = cstate.value();
        String callerExtension = call.getChatRoom().getPeerAddress().getUserName();
        CallEvent callEvent = new CallEvent(state,callerExtension);
        EventBus.getDefault().post(callEvent);
    }

    public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {
        Logger.e(TAG,stats.toString());
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
     * @param extension 1102
     * @return full address sip
     */
    private String genSipAddressByExtension(String extension) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sip:").append(extension).append("@").append(ConfigManager.getInstance().getDomain());
        return stringBuilder.toString();
    }

    /**
     * Login to VoIP Server (such as Aterisk, FreeSWITCH ...)
     * @param extension 10001
     * @param password  abcxzy
     * @throws LinphoneCoreException
     */
    public void loginVoIPServer(String extension, String password) throws LinphoneCoreException {
        String sipAddress = this.genSipAddressByExtension(extension);
        LinphoneCoreFactory lcFactory = LinphoneCoreFactory.instance();
        linphoneCore = lcFactory.createLinphoneCore(this, context);
        try {
            LinphoneAddress address = lcFactory.createLinphoneAddress(sipAddress);
            String username = address.getUserName();
            String domain = address.getDomain();
            if(password != null) {
                linphoneCore.addAuthInfo(lcFactory.createAuthInfo(username, password, (String)null, domain));
            }

            LinphoneProxyConfig proxyCfg = linphoneCore.createProxyConfig(sipAddress, domain, (String)null, true);
            proxyCfg.setExpires(2000);
            linphoneCore.addProxyConfig(proxyCfg);
            linphoneCore.setDefaultProxyConfig(proxyCfg);
            this.running = true;

            while(this.running) {
                linphoneCore.iterate();
                this.sleep(50);
            }

            linphoneCore.getDefaultProxyConfig().edit();
            linphoneCore.getDefaultProxyConfig().enableRegister(false);
            linphoneCore.getDefaultProxyConfig().done();

        } finally {
            Logger.e("LinphoneHandler","Shutting down linphone...");
            linphoneCore.destroy();
        }

    }

    private void sleep(int ms) {
        try {
            Thread.sleep((long)ms);
        } catch (InterruptedException var3) {
            Logger.e("LinphoneHandler","Interrupted!\nAborting");
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

    public void sendMessage(String raw) {

    }

    public void acceptCall() throws LinphoneCoreException {
        LinphoneCall currentCall = linphoneCore.getCurrentCall();
        linphoneCore.acceptCall(currentCall);
    }
}
