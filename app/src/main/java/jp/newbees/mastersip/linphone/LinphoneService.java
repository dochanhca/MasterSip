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
import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.event.call.SendingCallEvent;
import jp.newbees.mastersip.event.call.SpeakerEvent;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service{

    private Handler mHandler;
    private LinphoneHandler linphoneHandler;
    private final static String TAG = "LinphoneService";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG,"onCreate");
        EventBus.getDefault().register(this);
        mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        linphoneHandler = new LinphoneHandler(notifier, this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG,"OnStartCommand");
        SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
        loginToVoIP(sipItem);
        return super.onStartCommand(intent, flags, startId);
    }

    private void loginToVoIP(final SipItem sipItem) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Logger.e("LinephonService","Logging " + sipItem.getExtension() + " - " + sipItem.getSecret());
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
        Logger.e(TAG,"Stop Linphone Service");
    }

    /**
     * This method invoked by EventBus when user accept or reject a call
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

    /**
     * This method invoked by EventBus when enable or disable Speaker
     * @param speakerEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public final void onSpeakerEvent(SpeakerEvent speakerEvent) {
        linphoneHandler.enableSpeaker(speakerEvent.isEnable());
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
    private void handleAcceptCall(){
        try {
            linphoneHandler.acceptCall();
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

}
