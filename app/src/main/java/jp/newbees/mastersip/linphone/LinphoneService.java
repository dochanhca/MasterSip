package jp.newbees.mastersip.linphone;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.OpenH264DownloadHelperListener;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.LinphoneServicePresenter;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.ui.call.IncomingVideoChatActivity;
import jp.newbees.mastersip.ui.call.IncomingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.IncomingVoiceActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
import static android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.EXTRA_STATE_RINGING;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service implements LinphoneServicePresenter.IncomingCallListener {

    private static final String TAG = "LinphoneService";
    public static final int START_FROM_PUSH_NOTIFICATION = 1;
    public static final int START_FROM_ACTIVITY = 2;
    private static final String START_SERVICE_FROM = "START_SERVICE_FROM";

    private BroadcastReceiver receiverRingerModeChanged;

    private LinphoneServicePresenter incomingCallPresenter;

    private static LinphoneService instance;
    private BroadcastReceiver callStateChangeReceiver;

    private OpenH264DownloadHelperListener h264DownloadHelperListener = new OpenH264DownloadHelperListener() {

        @Override
        public void OnProgress(int current, int max) {
            if (current > max) {
                Logger.e("LinphoneService", "Download done");
                LinphoneHandler.getInstance().reloadMsPlugins(LinphoneService.this.getApplicationInfo().nativeLibraryDir);
                LinphoneService.this.restartApplication();
            }
        }

        @Override
        public void OnError(String s) {

        }

    };

    public void restartApplication() {
        Intent mStartActivity = new Intent(this, StartActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);

        stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("jp.newbees.mastersip");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void startLinphone(Context context) {
        LinphoneService.startLinphone(context, LinphoneService.START_FROM_ACTIVITY);
    }

    public static void startLinphone(Context context, int startFromPushNotification) {
        ConfigManager.getInstance().startServiceFrom(startFromPushNotification);
        Intent intent = new Intent(context, LinphoneService.class);
        context.startService(intent);
    }

    public static void stopLinphone(Context context) {
        Logger.e("LinphoneService", "Stopping service");
        Intent intent = new Intent(context, LinphoneService.class);
        context.stopService(intent);
    }

    public static boolean isReady() {
        return instance != null;
    }

    public static boolean isRunning() {
        if (null == instance) {
            return false;
        }else {
            return true;
        }
    }

    private static void destroyLinphoneService() {
        LinphoneService.instance = null;
    }

    private static void initInstance(LinphoneService instance) {
        LinphoneService.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG, "Linphone Service onCreate");
        incomingCallPresenter = new LinphoneServicePresenter(getApplicationContext(), this, h264DownloadHelperListener);
        incomingCallPresenter.registerCallEvent();

        LinphoneCoreFactory.instance().enableLogCollection(Constant.Application.DEBUG);
        LinphoneCoreFactory.instance().setDebugMode(Constant.Application.DEBUG, getApplication().getString(R.string.app_name));

        Handler mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        LinphoneHandler.createAndStart(getApplicationContext(), notifier);
        SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
        loginToVoIP(sipItem);
        registerReceiverRingerModeChanged();
        registerGSMCallBroadcastReceiver();
        LinphoneService.initInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG, "OnStartCommand");
        return START_NOT_STICKY;
    }

    private void loginToVoIP(final SipItem sipItem) {
        LinphoneHandler.getInstance().loginVoIPServer(sipItem);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.e("LinphoneService", "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverRingerModeChanged);
        unregisterReceiver(callStateChangeReceiver);
        incomingCallPresenter.unRegisterCallEvent();
        LinphoneHandler.getInstance().destroy();
        LinphoneService.destroyLinphoneService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        this.stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void incomingVoiceCall(UserItem caller, String callID) {
        IncomingVoiceActivity.startActivityWithNewTask(this, caller, callID);
    }

    @Override
    public void incomingVideoCall(UserItem caller, String callID) {
        IncomingVideoVideoActivity.startActivityWithNewTask(this, caller, callID);
    }

    @Override
    public void incomingVideoChatCall(UserItem caller, String callID) {
        IncomingVideoChatActivity.startActivityWithNewTask(this, caller, callID);
    }

    @Override
    public void didCheckCallError(int errorCode, String errorMessage) {
        Toast.makeText(this, "Error " + errorCode + " when check call : " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void registerReceiverRingerModeChanged() {
        receiverRingerModeChanged = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LinphoneHandler.getInstance().updateLocalRing();
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(receiverRingerModeChanged, filter);
    }

    private void registerGSMCallBroadcastReceiver() {

        callStateChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String state = extras.getString(TelephonyManager.EXTRA_STATE);
                    if (state.equals(EXTRA_STATE_RINGING)) {
                        Logger.e("LinphoneService", "Incoming call gsm");
                        LinphoneHandler.getInstance().handleIncomingCallGSM();
                    }else if(state.equals(EXTRA_STATE_IDLE)) {
                        Logger.e("LinphoneService", "Idle call");
                        LinphoneHandler.getInstance().handleIdleCallGSM();
                    }else if(state.equals(EXTRA_STATE_OFFHOOK)) {
                        Logger.e("LinphoneService", "Outgoing call gsm");
                        LinphoneHandler.getInstance().handleOutgoingCallGSM();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callStateChangeReceiver, filter);
    }
}
