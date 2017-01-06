package jp.newbees.mastersip.test;

import android.content.Context;

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

import jp.newbees.mastersip.linphone.LinphoneNotifier;
import jp.newbees.mastersip.linphone.RegisterVoIPManager;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class LinphoneHandler implements LinphoneCoreListener {
    private Context context;
    private boolean running;
    private LinphoneNotifier notifier;

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
        //NEU Incoming...
        //NEU Outgoing...
        //NEU Start Streaming
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
     * Login to VoIP Server (such as Aterisk, FreeSWITCH ...)
     * @param extension 10001
     * @param password  abcxzy
     * @throws LinphoneCoreException
     */
    public void loginVoIPServer(String extension, String password) throws LinphoneCoreException {
        extension = "sip:"+extension+"@"+ ConfigManager.getInstance().getDomain();
        LinphoneCoreFactory lcFactory = LinphoneCoreFactory.instance();
        LinphoneCore lc = lcFactory.createLinphoneCore(this, context);

        try {
            LinphoneAddress address = lcFactory.createLinphoneAddress(extension);
            String username = address.getUserName();
            String domain = address.getDomain();
            if(password != null) {
                lc.addAuthInfo(lcFactory.createAuthInfo(username, password, (String)null, domain));
            }

            LinphoneProxyConfig proxyCfg = lc.createProxyConfig(extension, domain, (String)null, true);
            proxyCfg.setExpires(2000);
            lc.addProxyConfig(proxyCfg);
            lc.setDefaultProxyConfig(proxyCfg);
            this.running = true;

            while(this.running) {
                lc.iterate();
                this.sleep(50);
            }

            lc.getDefaultProxyConfig().edit();
            lc.getDefaultProxyConfig().enableRegister(false);
            lc.getDefaultProxyConfig().done();

            while(lc.getDefaultProxyConfig().getState() != LinphoneCore.RegistrationState.RegistrationCleared) {
                lc.iterate();
                this.sleep(50);
            }

            lc.getDefaultProxyConfig().edit();
            lc.getDefaultProxyConfig().enableRegister(true);
            lc.getDefaultProxyConfig().done();

            while(lc.getDefaultProxyConfig().getState() != LinphoneCore.RegistrationState.RegistrationOk
                    && lc.getDefaultProxyConfig().getState() != LinphoneCore.RegistrationState.RegistrationFailed) {
                lc.iterate();
                this.sleep(50);
            }
        } finally {
            this.write("Shutting down linphone...");
            lc.destroy();
        }

    }

    private void sleep(int ms) {
        try {
            Thread.sleep((long)ms);
        } catch (InterruptedException var3) {
            this.write("Interrupted!\nAborting");
        }
    }

    public void stopMainLoop() {
        this.running = false;
    }

    private void write(String s) {
        this.notifier.notify(s);
    }

    public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
        //TEXT
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
}
