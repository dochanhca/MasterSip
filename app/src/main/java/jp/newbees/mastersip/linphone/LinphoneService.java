package jp.newbees.mastersip.linphone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service {

    private LinphoneHandler linphoneHandler;
    private static final String TAG = "LinphoneService";
    private BroadcastReceiver receiverRingerModeChanged;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG, "onCreate");
        Handler mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        if (LinphoneHandler.getInstance() == null) {
            linphoneHandler = LinphoneHandler.createAndStart(notifier, getApplicationContext());
        } else {
            linphoneHandler = LinphoneHandler.getInstance();
        }
        registerReceiverRingerMOdeChanged();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG, "OnStartCommand");
        if (!linphoneHandler.isRunning()) {
            SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
            loginToVoIP(sipItem);
        } else {
            EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_SUCCESS));
        }
        return START_NOT_STICKY;
    }

    private void loginToVoIP(final SipItem sipItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "Logging " + sipItem.getExtension() + " - " + sipItem.getSecret());
                linphoneHandler.loginVoIPServer(sipItem);
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
        linphoneHandler.destroy();
        unregisterReceiver(receiverRingerModeChanged);
        Logger.e(TAG, "Stop Linphone Service");
    }

    /**
     * Run when app be killed
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.e(TAG, "onTaskRemoved");
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    private void registerReceiverRingerMOdeChanged() {
        receiverRingerModeChanged = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (LinphoneHandler.getLinphoneCore() != null) {
                    linphoneHandler.updateLocalRing();
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(receiverRingerModeChanged, filter);
    }
}
