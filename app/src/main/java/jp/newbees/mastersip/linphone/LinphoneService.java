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
import android.widget.Toast;

import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.CenterIncomingCallPresenter;
import jp.newbees.mastersip.ui.call.IncomingVideoChatActivity;
import jp.newbees.mastersip.ui.call.IncomingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.IncomingVoiceActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service implements CenterIncomingCallPresenter.IncomingCallListener{

    private LinphoneHandler linphoneHandler;
    private static final String TAG = "LinphoneService";
    private BroadcastReceiver receiverRingerModeChanged;

    private CenterIncomingCallPresenter incomingCallPresenter;

    private static LinphoneService instance;

    public static void startLinphone(Context context) {
        Intent intent = new Intent(context, LinphoneService.class);
        context.startService(intent);
    }

    public static void stopLinphone(Context context) {
        Intent intent = new Intent(context, LinphoneService.class);
        context.stopService(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static boolean isReady() {
        return instance != null;
    }

    public static boolean isRunning() {
        if (instance == null || instance.linphoneHandler == null) {
            return false;
        }
        return instance.linphoneHandler.isRunning();
    }

    /**
     * @throws RuntimeException service not instantiated
     */
    public static LinphoneService instance()  {
        if (isReady()) return instance;

        throw new RuntimeException("LinphoneService not instantiated yet");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG, "onCreate");
        incomingCallPresenter = new CenterIncomingCallPresenter(getApplicationContext(), this);
        incomingCallPresenter.registerCallEvent();

        Handler mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        linphoneHandler = LinphoneHandler.createAndStart(notifier, getApplicationContext());
        SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
        loginToVoIP(sipItem);
        registerReceiverRingerModeChanged();
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG, "OnStartCommand");

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
        instance = null;
        unregisterReceiver(receiverRingerModeChanged);
        incomingCallPresenter.unRegisterCallEvent();
        linphoneHandler.destroy();
        super.onDestroy();
        Logger.e(TAG, "Stop Linphone Service");
    }

    @Override
    public void incomingVoiceCall(UserItem caller, String callID) {
        IncomingVoiceActivity.startActivity(this, caller, callID);
    }

    @Override
    public void incomingVideoCall(UserItem caller, String callID) {
        IncomingVideoVideoActivity.startActivity(this, caller, callID);
    }

    @Override
    public void incomingVideoChatCall(UserItem caller, String callID) {
        IncomingVideoChatActivity.startActivity(this, caller, callID);
    }

    @Override
    public void didCheckCallError(int errorCode, String errorMessage) {
        Toast.makeText(this, "Error "+errorCode+" when check call : "+errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void registerReceiverRingerModeChanged() {
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
